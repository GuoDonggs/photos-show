<script setup>
import {onMounted, ref} from "vue";

const loadNext = defineEmits(['load-next'])
const props = defineProps({
  load() {

  },
  color: {
    default: "#252525"
  },
  loadFinish: {
    default: false
  }
})

onMounted(() => {
  addScroll()
})

const jitterFiltering = ref({})

function addScroll() {
  window.addEventListener('scroll', () => {
    let element = document.querySelector('.bottom-box'); // 你的元素ID
    let position = element.getBoundingClientRect();

    // 检查滚动位置是否超过元素位置
    if (position.top <= window.innerHeight && !props.loadFinish) {
      // 触发的事件
      try {
        clearTimeout(jitterFiltering.value)
      } catch (e) {
      }
      jitterFiltering.value = setTimeout(() => {
        loadNext('load-next')
      }, 300)
    }
  })
}
</script>

<template>
  <div class="bottom-box">
    <div v-if="props.loadFinish" :style="{color:props.color}">
      <slot name="finish-text">
        到底喽
      </slot>
    </div>
    <div v-else>
      <slot name="load-text">
        loading...
      </slot>
    </div>
  </div>
</template>

<style lang="less" scoped>
.bottom-box {
  padding-bottom: 40px;
  text-align: center;
  font-size: 40px;
  font-family: AFKYS;
}
</style>