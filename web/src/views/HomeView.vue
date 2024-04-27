<script setup>
import TopNav from "@/components/TopNav.vue";
import {computed, onBeforeMount, reactive, ref} from "vue";
import axios from "axios";
import {imageApi} from "@/config/api.js";
import WaterfallImages from "@/components/WaterfallImages.vue";
import {systemStore} from "@/stores/systemStatus.js";
import ScrollLoad from "@/components/ScrollLoad.vue";
import LoadGif from "@/components/LoadGif.vue";

const carouselItems = ref([])
const system = systemStore()

const options = reactive({
  carouselHeight: '100vh',
  helloWord: '早安',
  firstLoad: true
})

const waterfallColNum = computed(() => {
  let width = window.innerWidth
  if (width <= 768) {
    return 3
  }
  if (width <= 1200) {
    return 4
  }
  if (width <= 1900) {
    return 5
  }
  return 6

})

onBeforeMount(() => {
  getHotPicture()
  getHelloTimeWord()
  getWaterfallImage()
  setTimeout(() => {
    if (carouselItems.value.length === 0) {
      options.firstLoad = false
    }
  }, 5000)
})

function getHotPicture() {
  if (carouselItems.value.length === 0) {
    axios.get(imageApi.hot, {params: {size: 5}})
        .then(res => {
          carouselItems.value = res.data.data
        })
  }
}

function getHelloTimeWord() {
  let hours = new Date().getHours()
  const word = [
    "深夜喽~<br/>看会毛毛睡觉啦",
    "呼噜噜~<br/>Z Z Z ~",
    "早安~<br/>困的话再眯一会叭~v~",
    "中午好<br/>有好好吃饭嘛",
    "下午好<br/>休息一下叭qvq",
    "晚上好<br/>一起来打电动嘛ovo",
  ]
  let i = hours / 4
  options.helloWord = word[Math.floor(i)]
}

const waterfallPage = ref(1)
const waterfallItem = ref([])

function getWaterfallImage() {
  axios.get(imageApi.list, {params: {page: waterfallPage.value, size: 20}})
      .then(res => {
        if (waterfallPage.value < 20 && res.data.data != null && res.data.data.length > 0) {
          waterfallItem.value = waterfallItem.value.concat(res.data.data)
          waterfallPage.value++
        } else {
          options.waterfallFinish = true
        }
      })
}

</script>

<template>
  <load-gif v-if="options.firstLoad"/>
  <top-nav :is-fixed="true"/>

  <!-- 主页轮播图 -->
  <div class="home-carousel">
    <!--  问安消息  -->
    <div class="fixed-text" v-html="options.helloWord"/>
    <!--  轮播图  -->
    <el-carousel :height="options.carouselHeight"
                 indicator-position="none">
      <el-carousel-item v-for="item in carouselItems" :key="item">
        <div class="carousel-item">
          <img :alt="item.keywords"
               :src="imageApi.raw + item.filePath"
               @load="options.firstLoad=false">
        </div>
      </el-carousel-item>
    </el-carousel>
  </div>

  <div class="waterfall-box">
    <div class="title">
      来吸毛吧<span v-if="system.options.isPc"> ヾ ￣▽)ゞ2333333</span>
    </div>
    <div class="waterfall">
      <waterfall-images v-if="waterfallItem.length > 0"
                        :column-num="waterfallColNum"
                        :data="waterfallItem"/>
    </div>
    <scroll-load :load-finish="options.waterfallFinish" @load-next="getWaterfallImage"/>
  </div>

  <div class="bottom-nav">
    <div>
      made & Design by <a href="https://space.bilibili.com/390958626?spm_id_from=333.1007.0.0">果冻</a>
    </div>
  </div>

</template>

<style lang="less" scoped src="@/less/homeView.less"/>