<script setup>

import BackImage from "@/components/BackImage.vue";
import {Delete, DocumentAdd, Select} from "@element-plus/icons-vue";
import {reactive, ref} from "vue";
import {ElMessage} from "element-plus";


const inputRef = ref(null)
const urls = reactive([])
const fileList = reactive([])
const keywords = reactive([])
const options = reactive({
  showAddKeyword: false,
  keywordTmp: ''
})

function checkFile() {

  const files = inputRef.value.files
  for (let i = 0; i < files.length; i++) {
    if (fileList.length + 1 >= 10) {
      ElMessage.warning("文件数量超过10个");
      return;
    }
    const file = files[i]
    if (file.size > 10 * 1024 * 1024) {
      ElMessage.warning("文件大小超过10MB")
      return
    }
    console.log(file.type)
    if (!(file.type + "").concat("image")) {
      ElMessage.warning("文件格式错误")
      return
    }
    // 去出重复文件
    if (fileList.find(i => i.name === file.name)) {
      ElMessage.warning("请勿重复选择文件")
      return;
    }
    urls.push(URL.createObjectURL(file))
    fileList.push(file)
  }
}

function deleteChose() {
  fileList.length = fileList.length - 1
  urls.length = urls.length - 1
}

function addKeyword() {

}


</script>

<template>
  <input v-show="false"
         ref="inputRef"
         accept="image/jpeg,image/png"
         multiple type="file" @change="checkFile">
  <back-image>
    <div class="mask"/>
    <div class="upload-box">
      <div class="upload-btn" @click="inputRef.click()">
        <ElIcon>
          <DocumentAdd/>
        </ElIcon>
        <div class="notice">10MB以下,单次10文件</div>
        <div class="notice">JPEG格式文件</div>
      </div>
      <div class="image-show">
        <div v-show="fileList.length > 0"
             class="title">
          已选择图片展示
        </div>
        <el-image v-for="i in urls"
                  :src="i"
                  class="item"
                  fit="cover"
        />
        <div v-show="fileList.length > 0"
             class="item delete"
             @click="deleteChose">
          <ElIcon>
            <Delete/>
          </ElIcon>
        </div>
      </div>
      <div v-show="fileList.length > 0" class="keyword-box">
        <div class="title">添加标签</div>
        <div class="item">
          {{ i }}
        </div>
        <div v-show="options.showAddKeyword" class="add-input item ">
          <div class="input">
            <el-input v-model="options.keywordTmp" :maxlength="6"
                      clearable placeholder="请输入关键字，2-8字符"/>
          </div>
          <el-button :icon="Select" circle type="primary" @click="addKeyword"/>
        </div>
        <div class="delete item">
          <ElIcon>
            <Delete/>
          </ElIcon>
        </div>
        <div class="item add-btn bgc1" @click="options.showAddKeyword = true">
          +
        </div>
      </div>
      <div class="clear-btn">
        <el-button type="primary" @click="">上传</el-button>
      </div>
    </div>
  </back-image>
</template>

<style lang="less" scoped src="@/less/uploadView.less"/>