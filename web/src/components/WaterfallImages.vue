<script setup>
import {onMounted, reactive, ref, watch} from "vue";
import {imageApi, loverApi} from "@/config/api.js";
import {Right} from "@element-plus/icons-vue";
import {useRouter} from "vue-router";
import {userStore} from "@/stores/user.js";
import {ElMessage} from "element-plus";
import {systemStore} from "@/stores/systemStatus.js";
import axiosPlugin from "@/config/axiosPlugin.js";

const props = defineProps({
  flag: {
    default: 'n'
  },
  data: {
    default: [
      [
        {
          uploadUser: 0,
          uploadDate: "",
          title: "",
          introduce: "",
          loverNum: 0,
          keywords: "",
          hasChecked: false,
          filePath: "",
        }
      ]
    ]
  },
  columnNum: {
    default: 4
  }
})


onMounted(() => {
  init()
})

watch(() => props.data.length, (value, oldValue, onCleanup) => {
  if (value > oldValue) {
    loadNextColum()
  }
})
const system = systemStore()
const router = useRouter()
const user = userStore()
// 储存列数据
const columns = reactive([])
// 初始化第一列,已触发自动加载


// 当前已放置图片的 index
const currentIndex = ref(1)

function init() {
  let cache = system.cache['waterfall' + props.flag]
  if (cache === undefined) {
    currentIndex.value = 0
    columns.length = 0
    for (let i = 0; i < props.columnNum; i++) {
      columns.push([])
    }
    columns[0] = [props.data[0]]
    currentIndex.value = 1
  } else {
    options.userCache = true
    options.minHeight = cache.height + 'px'
    currentIndex.value = cache.currentIndex
    for (let i = 0; i < cache.data.length; i++) {
      columns.push(cache.data[i])
    }
  }
}

const options = reactive({
  retry: false,
  loading: true,
  userCache: false,
  minHeight: '',
  loadCacheNum: 0
})

// 计算出下一个图片放置的列
function loadNextColum() {
  if (currentIndex.value >= props.data.length - 1) {
    // 判断是否为缓存
    if (!options.userCache) {
      system.cache['waterfall' + props.flag] = {
        currentIndex: currentIndex.value,
        data: columns,
        height: document.querySelector(".waterfall-images").clientHeight
      }
    }
    options.loading = false;
    return
  }
  options.loading = true;
  const doms = document.querySelectorAll(".waterfall-images .flex-container");
  const mindom = Array.from(doms).reduce((min, dom) =>
      dom.querySelector(".column-item").clientHeight
      < min.querySelector(".column-item").clientHeight ? dom : min
  );
  const i = parseInt(mindom.getAttribute("x-attr"));
  let data = props.data[currentIndex.value];
  if (data !== undefined) {
    columns[i].push(data);
  } else if (!options.retry) {
    options.retry = true
    loadNextColum()
  }
  options.retry = false
  currentIndex.value++;
}

const masks = reactive({})

// 显示图片上的遮罩层，遮罩层中含有图片的信息和点赞按钮
function showMask(id, tf) {
  if (masks[id] !== undefined) {
    masks[id].show = tf === undefined ? true : tf
  } else {
    masks[id] = {show: tf === undefined ? true : tf}
  }
}

// 适配手机点击
function allMaskFalse(id) {
  for (let key in masks) {
    if (key !== id) {
      if (masks[key] !== undefined) {
        masks[key].show = false
      } else {
        masks[key] = {show: false}
      }
    }
  }
  masks[id].show = true
}

// 加载是否已经点过赞
function loadIsLoved(fileId) {
  if (user.user.login) {
    axiosPlugin.getAxios(user.user.token)
        .get(loverApi.exists + fileId)
        .then(res => {
          if (masks[fileId] === undefined) {
            masks[fileId] = {}
          }
          masks[fileId].loved = res.data.data
        })
  }
}


function loveBtn(fileId, i) {
  if (!user.user.login) {
    ElMessage.error("请先登录")
    setTimeout(() => {
      router.push("/login")
    }, 2000)
    return
  }
  if (masks[fileId].loved) {
    axiosPlugin.getAxios(user.user.token)
        .post(loverApi.unloved, {}, {params: {fileId: fileId}})
        .then(res => {
          masks[fileId].loved = false
          if (i != null && i.loverNum - 1 >= 0) {
            i.loverNum -= 1
          }
        }, res => {
          ElMessage.error(res.response.data.msg)
        })
  } else {
    axiosPlugin.getAxios(user.user.token)
        .post(loverApi.love, {}, {params: {fileId: fileId}})
        .then(res => {
          masks[fileId].loved = true
          if (i != null) {
            i.loverNum += 1
          }
        }, res => {
          ElMessage.error(res.response.data.msg)
        })
  }

}

</script>

<template>
  <div v-loading="options.loading"
       :style="{'min-height':options.minHeight}"
       class="waterfall-images"
       element-loading-background="rgba(255,255,255,0.5)"
       element-loading-text="loading">
    <!--  flex 布局容器  -->
    <div v-for="(column,i)  in columns"
         :key="column"
         :x-attr="i"
         class="flex-container">
      <div class="column-item">
        <div v-for="item  in column"
             :key="item.fileId"
             class="image-item"
             @mouseenter="showMask(item.fileId)"
             @mouseleave="showMask([item.fileId], false)"
             @touchstart="allMaskFalse(item.fileId)">
          <img :alt="item.keywords" :src="imageApi.raw + item.filePath"
               @error="loadNextColum"
               @load="loadNextColum">
          <!-- 图片顶层遮罩 -->
          <div
              v-show="masks[item.fileId]!==undefined && masks[item.fileId].show"
              class="mask">
            <div class="title-area">
              <div class="text">
                {{ item.title.length === 0 ? "owo" : item.title }}
              </div>
              <div class="btn">
                <el-button :icon="Right" circle type="primary"
                           @click="$router.push({path:'/detail',query:{id:item.fileId}})"
                />
              </div>
            </div>
            <div class="love-area" @click="loveBtn(item.fileId,item)">
              <Transition mode="out-in" name="love-transition">
                <img v-if="masks[item.fileId] === undefined || !masks[item.fileId].loved"
                     :alt="item.loverNum"
                     src="@/assets/icon/unlove.svg"
                     @load="loadIsLoved(item.fileId)">
                <img v-else :alt="item.loverNum"
                     src="@/assets/icon/love.svg">
              </Transition>
              <span class="lover-num">
                {{ item.loverNum > 10000 ? (item.loverNum / 10000).toFixed(1) + "万" : item.loverNum }}
              </span>
            </div>
            <div class="upload-data">
              {{ new Date(item.uploadDate).toLocaleDateString() }}
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<style lang="less" scoped>
.waterfall-images {
  display: flex;

  .flex-container {
    flex: 1;
    margin: 0 1%;

    .column-item {
      width: 100%;
      margin-bottom: 20px;
      display: inline-block;

      .image-item {
        position: relative;
        margin-bottom: 20px;
        border-radius: 10px;
        overflow: hidden;


        img {
          width: 100%;
        }

        .mask {
          padding: 10px;
          display: flex;
          flex-direction: column;
          justify-content: space-between;
          align-items: center;
          position: absolute;
          top: 0;
          left: 0;
          height: 100%;
          width: 100%;
          z-index: 5;
          transition: all 0.5s;

          &:hover {
            background-color: #FFFFFF99;
            backdrop-filter: blur(10px);
          }

          .title-area {
            position: relative;
            width: 100%;
            display: flex;
            font-size: 20px;
            color: black;


            .text {
              flex: 1;
              text-align: center;
              overflow: hidden;
              white-space: nowrap;
              text-overflow: ellipsis;
            }

            .btn {
              position: absolute;
              right: 5px;
              z-index: 10;
            }
          }

          .love-area {
            width: 30%;
            cursor: pointer;
            text-align: center;

            img {
              width: 100%;
            }

            .lover-num {
              font-size: 21px;
              color: black;
            }
          }

          .upload-data {
            font-size: 18px;
            color: black;
          }
        }
      }

      @media screen and (max-width: 768px) {
        .image-item {
          margin-bottom: 10px;
        }
      }
    }
  }


}

.love-transition-enter-active,
.love-transition-leave-active {
  transition: all 1s ease-in-out;
}

.love-transition-leave-to {
  transform: rotateY(180deg);
}
</style>