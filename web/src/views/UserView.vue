<script setup>

import {userStore} from "@/stores/user.js";

import TopNav from "@/components/TopNav.vue";
import {onMounted, reactive, ref} from "vue";
import {imageApi, userApi} from "@/config/api.js";
import {systemStore} from "@/stores/systemStatus.js";
import {Delete, Right, User} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
import {useRouter} from "vue-router";
import axiosPlugin from "@/config/axiosPlugin.js";
import LoadGif from "@/components/LoadGif.vue";
import ScrollLoad from "@/components/ScrollLoad.vue";
import BackImage from "@/components/BackImage.vue";


const user = userStore()
const system = systemStore()
const router = useRouter()
const username = ref('')
const imageList = ref([])
const maskList = reactive([])
const options = reactive({
  imagePage: 1,
  imageListFinish: false,
  showUsernameDialog: false,
  axiosLoading: false,
  loadGif: true
})

onMounted(() => {
  listUserImage()
  setTimeout(() => {
    options.loadGif = false
  }, 3000)
})


function listUserImage() {
  if (!options.imageListFinish) {
    axiosPlugin.getAxios(user.user.token)
        .get(imageApi.listByUser, {params: {page: options.imagePage, size: 20}})
        .then(res => {
          let d = res.data.data
          if (d.length === 0) {
            options.imageListFinish = true
          } else {
            imageList.value = imageList.value.concat(d)
            if (d.length < 30) {
              options.imageListFinish = true;
            } else {
              options.imagePage++
            }
          }
        }, res => {
          ElMessage.error(res.response.data.msg)
        })
  }
}

function logout() {
  user.setToken(undefined)
  router.push("/")
  user.user.login = false
}

async function setUsername() {
  if (username.value.length < 2 || username.value.length > 6) {
    ElMessage.error("用户名长度为2-6")
    return
  }
  options.axiosLoading = true
  axiosPlugin.getAxios(user.user.token)
      .post(userApi.resetUsername, {}, {params: {username: username.value}})
      .then(res => {
        ElMessage.success("操作成功")
        user.user.info.username = username.value
        options.showUsernameDialog = false
        options.axiosLoading = false
      }, res => {
        ElMessage.error(res.response.data.msg)
        options.axiosLoading = false
      })
}

async function allMaskFalse(i) {
  maskList.forEach(item => {
    item = false
  })
  maskList[i] = true
}

async function deleteImage(fileId) {
  options.axiosLoading = true
  axiosPlugin.getAxios(user.user.token)
      .delete(imageApi.deleteFile + fileId)
      .then(res => {
        ElMessage.success("删除成功")
        imageList.value = imageList.value.filter(item => item.fileId !== fileId)
        options.axiosLoading = false
      }, res => {
        ElMessage.error(res.response.data.msg)
        options.axiosLoading = false
      })
}


</script>

<template>
  <load-gif v-if="options.loadGif"/>
  <back-image>
    <top-nav :is-fixed="false"/>
    <div v-loading.fullscreen.lock="options.axiosLoading" class="user-view">
      <div class="left-item">
        <span v-if="system.options.isPc">用户名：</span>
        <span>{{ user.user.info.username }}</span>
        <el-link class="link" type="primary" @click="options.showUsernameDialog = true">
          修改<span v-if="system.options.isPc">用户名</span>
        </el-link>
      </div>
      <div class="btn">
        <el-button class="logout" type="danger" @click="logout">退出登录</el-button>
        <el-button class="upload" type="primary" @click="router.push('/upload')">上传图片</el-button>
      </div>
    </div>
    <div class="file-show">
      <div class="title" @click="router.push('/upload')">已上传图片</div>
      <el-empty v-if="imageList.length === 0" description="你还没有上传图片哦"></el-empty>
      <div class="show-image">
        <div v-for="(item,index) in imageList"
             class="item"
             @mouseenter="maskList[index] = true"
             @mouseleave="maskList[index] = false"
             @touchstart="allMaskFalse(index)">
          <div class="image">
            <img :src="imageApi.raw + item.filePath" alt=""
                 @load="maskList[index] = false"/>
          </div>
          <ul v-if="maskList[index]" class="mask">
            <li class="to-detail">
              <el-button :icon="Delete"
                         circle
                         type="danger"
                         @click="deleteImage(item.fileId)"
              />
              <el-button :icon="Right"
                         circle
                         type="primary"
                         @click="router.push({path:'/detail',query:{id:item.fileId}})"/>
            </li>
            <li class="lover-num">点赞数：{{ item.loverNum }}</li>
            <li class="upload-date">
              上传日期：{{ new Date(item.uploadDate).toLocaleString() }}
            </li>
          </ul>
        </div>
      </div>
    </div>
    <scroll-load :color="'#fff'"
                 :load-finish="options.imageListFinish"
                 @load-next="listUserImage"/>
  </back-image>


  <!-- 修改用户名弹窗 -->
  <el-dialog
      v-model="options.showUsernameDialog"
      title="修改用户名"
      width="500"
  >
    <el-input v-model="username" :maxlength="6"
              :prefix-icon="User" clearable
              placeholder="请输入用户名, 2-6个字符"/>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="options.showUsernameDialog = false">取消</el-button>
        <el-button type="primary" @click="setUsername">
          确认
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style lang="less" scoped src="@/less/UserView.less"/>