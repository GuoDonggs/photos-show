<script setup>


import axios from "axios";
import {imageApi} from "@/config/api.js";
import {onMounted, reactive} from "vue";
import {systemStore} from "@/stores/systemStatus.js";

const props = defineProps({
  page: {
    default: 1
  }
})
const system = systemStore()
const options = reactive({
  bgImage: '',
})

onMounted(() => {
  getBgImage()
})

function getBgImage() {
  let cache = system.cache["u-bgImage"]
  if (cache === undefined) {
    axios.get(imageApi.list, {
      params: {
        page: props.page,
        size: 1
      }
    }).then((res) => {
      let url = res.data.data[0].filePath
      options.bgImage = imageApi.raw + url
      system.cache["u-bgImage"] = options.bgImage
    })
  } else {
    options.bgImage = cache
  }
}
</script>

<template>
  <div :style="{backgroundImage: 'url(' + options.bgImage + ')'}"
       class="back-ground-color">
    <slot>

    </slot>
  </div>
</template>

<style lang="less" scoped>
.back-ground-color {
  min-height: 100vh;
  background-size: cover;
  background-position: center;
}

</style>