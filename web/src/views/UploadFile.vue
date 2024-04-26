<script setup>

import BackImage from "@/components/BackImage.vue";
import {CloseBold, Delete, DocumentAdd, Select} from "@element-plus/icons-vue";
import {reactive, ref} from "vue";
import {ElMessage} from "element-plus";
import axiosPlugin from "@/config/axiosPlugin.js";
import {userStore} from "@/stores/user.js";
import {useRouter} from "vue-router";
import {imageApi} from "@/config/api.js";

const user = userStore()
const router = useRouter()
const inputRef = ref(null)
const urls = reactive([])
const fileList = reactive([])
const keywords = reactive([])
const titleIntro = reactive({
  title: '',
  intro: ''
})
const options = reactive({
  showAddKeyword: false,
  keywordTmp: '',
  loading: false
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

function deleteLastImage() {
  fileList.length = fileList.length - 1
  urls.length = urls.length - 1
  if (fileList.length === 0) {
    keywords.length = 0
  }
}

function deleteLastKeyword() {
  keywords.length = keywords.length - 1
}


function addKeyword() {
  if (keywords.length >= 8) {
    ElMessage.warning("关键字数量超过8个")
    return
  }
  if (options.keywordTmp.length < 1 || options.keywordTmp.length > 8) {
    ElMessage.warning("关键字长度不符合要求")
    return
  }
  // 去出重复关键字
  if (keywords.find(i => i === options.keywordTmp)) {
    ElMessage.warning("请勿重复添加关键字")
    return
  }
  keywords.push(options.keywordTmp)
  options.showAddKeyword = false
  options.keywordTmp = ''
}

function submit() {
  if (fileList.length === 0) {
    ElMessage.warning("请选择文件")
    return
  }
  if (keywords.length === 0) {
    ElMessage.warning("请添加关键字")
    return;
  }
  const formData = new FormData()

  for (let i = 0; i < fileList.length; i++) {
    formData.append('files', fileList[i])
  }
  formData.append('keywords', keywords)
  if (fileList.length === 1) {
    formData.append('title', titleIntro.title)
    formData.append('introduce', titleIntro.intro)
  }
  options.loading = true
  axiosPlugin.getAxios(user.user.token)
      .post(imageApi.upload, formData)
      .then(res => {
        ElMessage.success("上传成功")
        router.push('/user')
        options.loading = false
      }, res => {
        ElMessage.error("上传失败，" + res.response.data.msg)
        options.loading = false
      })
}

</script>

<template>
  <input v-show="false"
         ref="inputRef"
         accept="image/jpeg,image/png"
         multiple type="file" @change="checkFile">
  <back-image>
    <div v-loading.fullscreen.lock="options.loading"
         class="mask"
         element-loading-background="rgba(122, 122, 122, 0.8)"
    />
    <div class="upload-box">
      <div class="upload-btn" @click="inputRef.click()">
        <ElIcon>
          <DocumentAdd/>
        </ElIcon>
        <div class="notice">10MB以下,单次10文件</div>
        <div class="notice">JPEG格式文件</div>
      </div>
      <div v-show="fileList.length > 0" class="image-show">
        <div class="title">
          已选择图片展示
        </div>
        <div v-show="fileList.length > 1" class="tips">
          tips: 选择多张图片时无法添加标题和介绍
        </div>
        <el-image v-for="i in urls"
                  :src="i"
                  class="item"
                  fit="cover"
        />
        <div v-show="fileList.length > 0"
             class="item delete"
             @click="deleteLastImage">
          <ElIcon>
            <Delete/>
          </ElIcon>
        </div>
      </div>
      <div v-show="fileList.length > 0" class="keyword-box">
        <div class="title">添加标签</div>
        <div v-show="fileList.length > 1" class="tips">tips: 添加至所有已选择图片</div>
        <div v-for="(i,index) in keywords" :class="'bgc' + index%7" class="item">
          {{ i }}
        </div>
        <div v-show="options.showAddKeyword" class="add-input item ">
          <div class="input">
            <el-input v-model="options.keywordTmp" :maxlength="6"
                      clearable
                      placeholder="请输入关键字，2-8字符"
                      show-word-limit @keydown.enter="addKeyword"/>
          </div>
          <el-button :icon="Select" circle type="primary" @click="addKeyword"/>
          <el-button :icon="CloseBold" circle type="danger" @click="options.showAddKeyword = false"/>
        </div>
        <div v-show="keywords.length > 0" class="delete item" @click="deleteLastKeyword">
          <ElIcon>
            <Delete/>
          </ElIcon>
        </div>
        <div v-if="keywords.length < 8"
             class="item add-btn bgc1"
             @click="options.showAddKeyword = true">
          +
        </div>
      </div>
      <div v-if="fileList.length === 1" class="intro">
        <div class="title">
          标题 & 简介
        </div>
        <el-input v-model="titleIntro.title" :maxlength="20"
                  class="title-input" placeholder="请输入标题"
                  show-word-limit type="textarea"/>
        <el-input v-model="titleIntro.intro" :maxlength="300"
                  placeholder="请输入介绍"
                  show-word-limit type="textarea"/>
      </div>
      <div v-if="fileList.length > 0" class="upload-submit">
        <el-button class="btn" size="large"
                   type="primary" @click="submit">确认上传
        </el-button>
      </div>
    </div>
    <div class="mask"/>
  </back-image>
</template>

<style lang="less" scoped src="@/less/uploadView.less"/>