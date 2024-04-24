<script setup>

import TopNav from "@/components/TopNav.vue";
import {onMounted, reactive, ref, watch} from "vue";
import axios from "axios";
import {imageApi} from "@/config/api.js";
import {systemStore} from "@/stores/systemStatus.js";
import WaterfallImages from "@/components/WaterfallImages.vue";
import ScrollLoad from "@/components/ScrollLoad.vue";

const props = defineProps({
  t: {
    default: ''
  }
})

const options = reactive({
  bgImage: '',
  searchPage: 1,
  searchFinish: false
})

const system = systemStore()
const imageList = ref([])
const keywordList = reactive({})

function getBgImage() {
  let cache = system.cache["bgImage"]
  if (cache === undefined) {
    axios.get(imageApi.list, {
      params: {
        page: 1,
        size: 1
      }
    }).then((res) => {
      let url = res.data.data[0].filePath
      options.bgImage = imageApi.raw + url
      system.cache["bgImage"] = options.bgImage
    })
  } else {
    options.bgImage = cache
  }
}

onMounted(() => {
  getBgImage()
  window.addEventListener('load', (e) => {
    let html = document.querySelector('#app')
    html.style.backgroundImage = "url(" + "options.bgImage" + ")"
  })
  search()
})

// 监控传入的搜索内容
watch(() => props.t, () => {
  clearSearch()
  search()
})

function clearSearch() {
  options.searchPage = 1
  options.searchFinish = false
  imageList.value = []
}

function search() {
  if (!options.searchFinish) {
    axios.get(imageApi.search + props.t, {params: {page: options.searchPage, size: 30}})
        .then((res) => {
          let r = res.data.data
          if (r.length === 0) {
            options.searchFinish = true
          } else {
            if (r.length < 30) {
              options.searchFinish = true
            } else {
              options.searchPage++
            }
            imageList.value = imageList.value.concat(r)
            // 添加关键词
            for (let i = 0; i < r.length; i++) {
              addKeywordList(r[i].keywords)
            }
            options.searchPage++
          }
        })
  }
}

async function addKeywordList(keyword) {
  let arr = keyword.split(',')
  for (let i = 0; i < arr.length; i++) {
    if (keywordList[arr[i]] === undefined) {
      keywordList[arr[i]] = i
    }
  }
}

</script>

<template>
  <div :style="{backgroundImage: 'url(' + options.bgImage + ')'}" class="bg-image">
    <top-nav :is-fixed="false"/>
    <div class="keyword-list">
      <div v-for="(value,key) in keywordList"
           :class="'bgc' + value%7"
           class="item" @click="$router.push({path:'/search',query:{t:key}})">
        {{ key }}
      </div>
    </div>
    <div class="waterfall-box">
      <div v-if="imageList.length > 0" class="waterfall">
        <waterfall-images :column-num="system.options.isPc?5:3" :data="imageList"
                          :flag="'search' + props.t + imageList.length"/>
      </div>
      <el-empty v-if="options.searchFinish && imageList.length ===0"
                class="empty" description="没有搜索到哦"/>
    </div>
    <scroll-load v-if="!(imageList.length ===0 && options.searchFinish)"
                 :color="'#fff'"
                 :load-finish="options.searchFinish"
                 @load-next="search"/>
  </div>
</template>

<style lang="less" scoped src="@/less/seachView.less"/>