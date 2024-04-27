<script setup>

import {systemStore} from "@/stores/systemStatus.js";
import {userStore} from "@/stores/user.js";
import {onMounted, reactive, ref} from "vue";
import axiosPlugin from "@/config/axiosPlugin.js";
import {imageApi, loverApi} from "@/config/api.js";
import BackImage from "@/components/BackImage.vue";
import TopNav from "@/components/TopNav.vue";
import {ElMessage} from "element-plus";
import {useRouter} from "vue-router";
import {Back, Right} from "@element-plus/icons-vue";

const props = defineProps({
  f: {
    default: '/'
  }
})
const user = userStore()
const system = systemStore()
const keywords = ref({})
const router = useRouter()
const options = reactive({
  currentFile: {},
  currentIndex: -1,
  currentLover: false,
  cacheFileList: [],
  preImageUrls: [],
  downloadBlob: ''
})

onMounted(() => {
  let i = system.imageList.current
  options.cacheFileList = system.imageList.list
  system.imageList.list.findIndex((item, index) => {
    options.preImageUrls.push(imageApi.raw + item.filePath)
    if (item.fileId === i) {
      options.currentIndex = index
      options.currentFile = item
      addKeywordList(item.keywords)
    }
  })

  if (i !== -1) {
    isLoved()
  } else {
    ElMessage.error('发生错误，图片未在缓存中，请重试')
    router.push(props.f)
  }
})

function isLoved() {
  if (user.user.login) {
    axiosPlugin.getAxios(user.user.token)
        .get(loverApi.exists + options.currentFile.fileId)
        .then(res => {
          options.currentLover = res.data.data
        })
  }
}

async function addKeywordList(keyword) {
  let arr = keyword.split(',')
  keywords.value = {}
  for (let i = 0; i < arr.length; i++) {
    if (keywords.value[arr[i]] === undefined) {
      keywords.value[arr[i]] = i
    }
  }
}

function currentIndexChange(isPre) {
  if (isPre) {
    if (options.currentIndex - 1 >= 0) {
      options.currentIndex -= 1;
      options.currentFile = options.cacheFileList[options.currentIndex]
      addKeywordList(options.currentFile.keywords)
      isLoved()
    } else {
      options.currentIndex = options.cacheFileList.length - 1
    }
  } else {
    console.log(options.currentIndex)
    if (options.currentIndex + 1 < options.cacheFileList.length) {
      options.currentIndex += 1;
      options.currentFile = options.cacheFileList[options.currentIndex]
      addKeywordList(options.currentFile.keywords)
      isLoved()
    } else {
      options.currentIndex = 0
    }
  }
}


</script>

<template>
  <back-image :page="4">
    <top-nav :blur="true" :is-fixed="false"/>
    <div class="detail-view">
      <div class="img-container">
        <el-button
            :icon="Back"
            circle @click="currentIndexChange(true)"/>
        <el-image :initial-index="options.currentIndex"
                  :preview-src-list="options.preImageUrls"
                  :preview-teleported="true"
                  :src="imageApi.raw + options.currentFile.filePath"
                  class="img"
                  fit="contain"/>
        <el-button
            :icon="Right"
            circle
            @click="currentIndexChange(false)"/>
      </div>
      <div class="info-container">
        <div class="keyword-list">
          <div v-for="(value,key) in keywords"
               :class="'bgc' + value%7"
               class="item" @click="$router.push({path:'/search',query:{t:key}})">
            {{ key }}
          </div>
        </div>
        <div class="title item1">
          <span v-if="options.currentFile.title !== undefined && options.currentFile.title.length !== 0">
               {{ options.currentFile.title }}
          </span>
          <el-skeleton v-else/>
        </div>
        <div class="introduce item1">
          <span v-if="options.currentFile.introduce !== undefined && options.currentFile.introduce.length !== 0">
              {{ options.currentFile.introduce }}
          </span>
          <el-skeleton v-else/>
        </div>
        <div class="upload-date item1">
          上传日期： {{ new Date(options.currentFile.uploadDate).toLocaleString() }}
        </div>
      </div>
    </div>
  </back-image>
</template>

<style lang="less" scoped src="@/less/detailView.less"/>