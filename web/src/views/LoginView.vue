<script setup>
import {defineProps, onBeforeMount, reactive} from 'vue'
import {imageApi, userApi, verifyApi} from '@/config/api.js'
import axios from 'axios'
import {ElMessage} from 'element-plus'
import {useRouter} from 'vue-router'
import {userStore} from "@/stores/user.js";
import {systemStore} from "@/stores/systemStatus.js";
import BackImage from "@/components/BackImage.vue";


const user = userStore()
const router = useRouter()
const system = systemStore()
const props = defineProps({
  f: {
    default: undefined
  }
})

onBeforeMount(() => {
  getBgImage()
  loadRemember()
  if (user.isLogin()) {
    ElMessage.warning('已登录，无需重复登录')
    setTimeout(() => {
      if (props.f == null) {
        router.push('/')
      } else {
        router.push(props.f)
      }
    }, 1000)
  }
})

const data = reactive({
  email: '',
  password: '',
  verifyId: "",
  verifyCode: ''
})

const option = reactive({
  loading: false,
  rememberPasswd: false,
  bgImage: '',
  surePasswd: '',
  hasError: false,
  errorMessage: '',
  imageVerifyData: '',
  verifyCodeBtn: {
    text: '获取验证码',
    hidden: false,
    disable: false
  }
})

function loadRemember() {
  let pd = JSON.parse(localStorage.getItem("rem"))
  if (pd !== null) {
    data.email = pd.email
    data.password = pd.password
    option.rememberPasswd = true
  }
}

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
      option.bgImage = imageApi.raw + url
      system.cache["bgImage"] = option.bgImage
    })
  } else {
    option.bgImage = cache
  }
}

function emailTest() {
  // 邮箱格式验证
  data.email = data.email.trim()
  const regex = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/
  if (!regex.test(data.email)) {
    option.hasError = true
    option.errorMessage = '邮箱格式错误'
    return false
  } else {
    option.hasError = false
    option.errorMessage = ''
    return true
  }
}

function passwordTest() {
  // 密码格式验证
  data.password = data.password.trim()
  const regex = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z=;*!@#$%^&:<>,./?]{6,}$/
  if (!regex.test(data.password) || 20 < data.password.length || data.password.length < 6) {
    option.hasError = true
    option.errorMessage = '6-19位密码字母+数字，可选特殊字符：=;*!@#$%^&:<>,./?'
    return false
  } else {
    option.hasError = false
    option.errorMessage = ''
    return true
  }
}

function submitPasswordTest() {
  // 确认密码格式验证
  option.surePasswd = option.surePasswd.trim()
  if (option.surePasswd !== data.password) {
    option.hasError = true
    option.errorMessage = '两次输入的密码不一致'
    return false
  } else {
    option.hasError = false
    option.errorMessage = ''
    return true
  }
}

function verifyCodeTest() {
  // 验证码格式验证
  if (data.verifyCode !== undefined && data.verifyCode.length >= 4) {
    option.hasError = false
    return true
  } else {
    option.hasError = true
    option.errorMessage = '请填写验证码'
    return false
  }
}

function getImageCode() {
  if (!emailTest()) {
    ElMessage.warning("请先正确输入邮箱")
  } else {
    axios.get(verifyApi.getImageCode, {params: {to: data.email}})
        .then(res => {
          data.verifyId = res.data.data.uuid
          option.imageVerifyData = res.data.data.data
          option.verifyCodeBtn.hidden = true
        })
  }
}

function sendVerifyCode() {
  let res = emailTest()
  if (res) {
    axios.get(verifyApi.getEmailCode, {params: {to: data.email}}).then(
        (res) => {
          data.verifyId = res.data.data
          ElMessage.success('验证码已发送至邮箱')
        },
        (res) => {
          ElMessage.error(res.response.data.msg)
        }
    )
    // 发送验证码
    option.verifyCodeBtn.disable = true
    option.verifyCodeBtn.text = 60
    let si = setInterval(() => {
      option.verifyCodeBtn.text -= 1
      if (option.verifyCodeBtn.text <= 0) {
        clearInterval(si)
        option.verifyCodeBtn.disable = false
        option.verifyCodeBtn.text = '获取验证码'
      }
    }, 1000)
  }
}

function login() {
  option.loading = true
  let r = passwordTest()
  r = r ? emailTest() : false
  r = r ? verifyCodeTest() : false;
  if (r) {
    axios.post(userApi.login, {
      email: data.email,
      password: data.password,
      verifyCode: data.verifyCode,
      verifyId: data.verifyId
    }).then(
        (res) => {
          localStorage.setItem("rem", JSON.stringify({
            email: data.email,
            password: data.password
          }))
          let r = res.data.data
          if (res.data.code === 1) {
            user.setToken(r.token)
            user.setInfo({username: r.username, uid: r.uid})
            option.loading = false
            ElMessage.success('登录成功,即将跳转')
            setTimeout(() => {
              if (props.f === undefined) {
                router.push('/')
              } else {
                router.push(props.f)
              }
            }, 2000)
          }
        },
        (res) => {
          option.loading = false
          ElMessage.error(res.response.data.msg)
        }
    )
  } else {
    option.loading = false;
  }
  // 登录
}

function register() {
  option.loading = true
  let r = emailTest()
  r = r ? passwordTest() : r
  r = r ? submitPasswordTest() : r
  r = r ? verifyCodeTest() : r
  if (r) {
    axios.post(userApi.register, {
      email: data.email,
      password: data.password,
      verifyId: data.verifyId,
      verifyCode: data.verifyCode
    }).then(
        res => {
          option.loading = false
          user.setToken(res.data.data.token)
          user.setInfo({username: res.username, uid: res.uid})
          ElMessage.success('注册成功,即将跳转')
          setTimeout(() => {
            router.push('/')
          }, 2000)
        },
        res => {
          option.loading = false
          ElMessage.error(res.response.data.msg)
        })
  } else {
    option.loading = false
  }
}

function resetPassword() {
  option.loading = true
  let r = emailTest()
  r = r ? passwordTest() : r
  r = r ? submitPasswordTest() : r
  r = r ? verifyCodeTest() : r
  if (r) {
    axios.post(userApi.resetPasswd, {
      email: data.email,
      password: data.password,
      verifyId: data.verifyId,
      verifyCode: data.verifyCode
    }).then(
        res => {
          option.loading = false
          ElMessage.success('密码重置成功')
          data.password = ""
          data.email = ""
          option.surePasswd = ""
          data.verifyCode = ""
          data.verifyId = ""
        },
        res => {
          option.loading = false
          ElMessage.error(res.response.data.msg)
        })
  } else {
    option.loading = false
  }

}
</script>

<template>
  <back-image>
    <div class="p-m"/>
    <div v-loading.fullscreen.load="option.loading" class="login-container">
      <!--  登录  -->
      <el-carousel
          :autoplay="false"
          arrow="always"
          height="auto"
          indicator-position="none"
          motion-blur
      >
        <el-carousel-item style="height: 500px">
          <div class="login">
            <div class="title">用户登录</div>
            <el-input
                v-model="data.email"
                class="item"
                clearable
                maxlength="20"
                placeholder="请输入邮箱"
                size="large"
                @change="emailTest"
            />
            <el-input
                v-model="data.password"
                class="item"
                clearable
                maxlength="20"
                placeholder="请输入密码"
                show-password
                size="large"
                type="password"
                @change="passwordTest"
            />
            <div class="item verify-code">
              <el-input
                  v-model="data.verifyCode"
                  class="input"
                  clearable
                  maxlength="8"
                  placeholder="请输入验证码"
                  size="large"
                  @change="verifyCodeTest"
              />
              <el-button
                  v-if="!option.verifyCodeBtn.hidden"
                  class="get-verify"
                  size="large"
                  type="success"
                  @click="getImageCode">
                获取验证码
              </el-button>
              <img v-else
                   :src="'data:image;base64,' +option.imageVerifyData" alt="" class="verify-image"
                   @click="getImageCode"/>
            </div>
            <div class="option item">
              <el-checkbox v-model="option.rememberPasswd">记住密码</el-checkbox>
              <el-link type="primary" @click="$router.push('/forget/passwd')">忘记密码</el-link>
            </div>
            <!--     确认登录按钮     -->
            <el-button class="item submit" size="large" type="primary" @click="login"
            >确认登录
            </el-button>
            <!--    错误警告框      -->
            <el-alert v-if="option.hasError" :closable="false" :title="option.errorMessage" class="item" type="error"/>
          </div>
        </el-carousel-item>
        <!--   注册   -->
        <el-carousel-item style="height: 500px">
          <div class="register">
            <div class="title">用户注册</div>
            <el-input
                v-model="data.email"
                class="item"
                clearable
                maxlength="20"
                placeholder="请输入邮箱"
                size="large"
                @change="emailTest"
            />
            <el-input
                v-model="data.password"
                class="item"
                clearable
                maxlength="20"
                placeholder="请输入密码"
                show-password
                size="large"
                type="password"
                @change="passwordTest"
            />
            <el-input
                v-model="option.surePasswd"
                class="item"
                clearable
                maxlength="20"
                placeholder="再次输入密码"
                show-password
                size="large"
                type="password"
                @change="submitPasswordTest"
            />
            <div class="verify-code item">
              <el-input
                  v-model="data.verifyCode"
                  class="input"
                  clearable
                  maxlength="8"
                  placeholder="请输入验证码"
                  size="large"
                  @change="verifyCodeTest"
              />
              <el-button
                  :disabled="option.verifyCodeBtn.disable"
                  class="get-verify"
                  size="large"
                  type="success"
                  @click="sendVerifyCode"
              >
                {{ option.verifyCodeBtn.text }}
              </el-button>
              <!--    错误警告框      -->
            </div>
            <el-button class="item submit" size="large" type="primary" @click="register"
            >确认注册
            </el-button>
            <el-alert v-if="option.hasError" :closable="false" :title="option.errorMessage" class="item" type="error"/>
          </div>
        </el-carousel-item>
        <!-- 找回密码 -->
        <el-carousel-item style="height: 500px">
          <div class="register">
            <div class="title">找回密码</div>
            <el-input
                v-model="data.email"
                class="item"
                clearable
                maxlength="20"
                placeholder="请输入邮箱"
                size="large"
                @change="emailTest"
            />
            <el-input
                v-model="data.password"
                class="item"
                clearable
                maxlength="20"
                placeholder="请输入密码"
                show-password
                size="large"
                type="password"
                @change="passwordTest"
            />
            <el-input
                v-model="option.surePasswd"
                class="item"
                clearable
                maxlength="20"
                placeholder="再次输入密码"
                show-password
                size="large"
                type="password"
                @change="submitPasswordTest"
            />
            <div class="verify-code item">
              <el-input
                  v-model="data.verifyCode"
                  class="input"
                  clearable
                  maxlength="8"
                  placeholder="请输入验证码"
                  size="large"
                  @change="verifyCodeTest"
              />
              <el-button
                  :disabled="option.verifyCodeBtn.disable"
                  class="get-verify"
                  size="large"
                  type="success"
                  @click="sendVerifyCode"
              >
                {{ option.verifyCodeBtn.text }}
              </el-button>
              <!--    错误警告框      -->
            </div>
            <el-button class="item submit" size="large" type="primary" @click="resetPassword">
              找回密码
            </el-button>
            <el-alert v-if="option.hasError" :closable="false" :title="option.errorMessage" class="item" type="error"/>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>
  </back-image>
</template>

<style lang="less" scoped src="@/less/loginView.less"/>
