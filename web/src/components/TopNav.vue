<script setup>

import {reactive, ref} from "vue";
import {Right, Search} from "@element-plus/icons-vue";
import {useRouter} from "vue-router";
import axios from "axios";
import {keywordApi} from "@/config/api.js";
import {ElMessage} from "element-plus";
import {userStore} from "@/stores/user.js";
import {systemStore} from "@/stores/systemStatus.js";

const props = defineProps({
  isFixed: {
    type: Boolean,
    default: false
  }
})

const system = systemStore()
const router = useRouter()
const user = userStore()
const searchText = ref("")
const searchTip = ref([])
const options = reactive({
  noviceGuide: localStorage.getItem("novice") !== "true"
})

// 进行搜索
function toSearch() {
  if (searchText.value == null || searchText.value.length === 0) {
    ElMessage.warning('请输入搜索内容')
  }
  router.push({path: '/search', query: {t: searchText.value}})
}

// 异步获取提示
function querySearchAsync(queryString, cb) {
  if (queryString.trim().length === 0) {
    cb([])
    return
  }
  axios.get(keywordApi.search + queryString).then(res => {
    let data = res.data.data
    let cb_data = []
    for (let i = 0; i < data.length; i++) {
      cb_data[i] = {value: data[i].keyword}
    }
    cb(cb_data)
  })
}

// 点击提示后的操作
function handleSelect() {
  // 跳转到搜索页
  router.push({path: '/search', query: {t: searchText.value}})
}

function finishNovice() {
  localStorage.setItem("novice", "true")
}
</script>


<template>
  <div :class="{'fixed': props.isFixed}" class="top-nav">
    <div class="left-item">
      <h3 class="logo-title" @click="router.push('/')">
        毛 <span v-if="system.options.isPc">绒 墙</span>
      </h3>
      <div class="search-box">
        <!--    强制el搜索框圆角    -->
        <div class="item">
          <el-autocomplete
              v-model="searchText"
              :debounce="800"
              :fetch-suggestions="querySearchAsync"
              :fit-input-width="true"
              :prefix-icon="Search"
              class="search-input"
              clearable
              placeholder="请输入关键字"
              placement="bottom"
              @select="handleSelect"
              @keydown.enter.="toSearch"
          />
        </div>
        <!--    搜索按钮    -->
        <el-button :icon="Right" circle
                   class="btn"
                   type="primary" @click="toSearch"/>
      </div>
    </div>
    <div class="right-item">
      <div v-if="!user.user.login" class="item login" @click="$router.push('/login')">
        请 登 录
      </div>
      <div v-else class="user item" @click="router.push('/user')">
        {{ user.user.info.username }}
      </div>
    </div>
  </div>

  <!-- 新手引导 -->
  <el-tour
      v-model="options.noviceGuide"
      @finish="finishNovice">
    <el-tour-step
        v-if="user.user.login"
        :next-button-props="{children: '下一步'}"
        :prev-button-props="{children: '上一步'}"
        description="点击用户名，可以进入用户中心、上传文件、管理文件"
        target=".user"
        title="用户中心"
    />
    <el-tour-step
        v-if="!user.user.login"
        :next-button-props="{children: '下一步'}"
        :prev-button-props="{children: '上一步'}"
        description="点击未登录，可以进入登录页面，上传文件点赞需要登录哦"
        target=".login"
        title="用户中心"
    />
    <el-tour-step
        :next-button-props="{children: '下一步'}"
        :prev-button-props="{children: '上一步'}"
        description="点这里可以返回首页"
        target=".logo-title"
        title="Tips"/>
    <el-tour-step
        :next-button-props="{children: '完 成'}"
        :prev-button-props="{children: '上一步'}"
        description="在这里可以搜索毛毛照片哦"
        target=".search-input"
        title="Tips"
    />
  </el-tour>
</template>

<style lang="less" scoped>

@nav-height-pc: 60px;
.fixed {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 10;
}

.top-nav {
  display: flex;
  padding: 0 20px;
  width: 100%;
  height: @nav-height-pc;
  background-color: rgba(224, 224, 224, 0.4);

  &:hover {
    background-color: rgba(255, 255, 255, 0.6);
    backdrop-filter: blur(10px);
    transition: all 1s;
  }
}

@media screen and (max-width: 768px) {
  .top-nav {
    padding: 0 10px;
    background-color: rgba(255, 255, 255, 0.4);
    backdrop-filter: blur(10px);
    justify-content: space-around;
  }
}

.left-item {
  flex: 1;
  display: flex;
  align-items: center;

  .logo-title {
    font-size: 26px;
    color: #fff;
    font-family: "AFKYS";
    cursor: pointer;
    white-space: nowrap;
    overflow: hidden;
  }

  .search-box {
    margin-left: 40px;
    display: flex;
    align-items: center;
    width: 40%;
    min-width: 300px;

    .item {
      width: 100%;
      border-radius: 30px;
      overflow: hidden;

      :deep(.el-autocomplete) {
        width: 100%;
      }
    }

    .btn {
      margin-left: 10px;
    }
  }

  @media screen and (max-width: 768px) {
    .search-box {
      flex: 1;
      margin: 0 20px;
      width: auto;
      min-width: 200px;
    }

    .btn {
      display: none;
    }
  }
}

@media screen and (min-width: 769px) {
  .right-item {
    flex: 1;
  }
}

.right-item {
  display: flex;

  justify-content: flex-end;
  align-items: center;
  max-width: 30%;


  .item {
    color: #424242;
    font-size: 24px;
    font-weight: bolder;
    cursor: pointer;
    font-family: AFKYS;
  }

  .login {
    border-radius: 30px;
    padding: 0 10px;
    height: 30px;
    line-height: 30px;
    margin-right: 20px;
    background-color: rgba(255, 255, 255, 0.2);
  }

  .user {
    font-size: 30px;
    text-overflow: ellipsis;
    white-space: nowrap;
    overflow: hidden;
  }
}

@media screen and (max-width: 768px) {
  .right-item {
    justify-content: flex-start;
  }
}


</style>