const root = "https://image.owofurry.fun:11000"

// const root = window.origin + "/api"

export const rootApi = root;

export const imageApi = {
    list: root + "/image/list",
    listByUser: root + '/image/list/by_user',
    hot: root + "/image/hot",
    // /raw/{filePath}
    raw: root + "/raw",
    // /image/search/{str}
    search: root + '/image/search/',
    // /delete/{fileId}
    deleteFile: root + "/delete/",
    upload: root + '/upload/image'
}

export const keywordApi = {
    // /keyword/search/{keyword}
    search: root + '/keyword/search/'
}

export const userApi = {
    login: root + "/user/login",
    register: root + "/user/register",
    check: root + "/user/check",
    resetPasswd: root + "/user/reset-password",
    resetUsername: root + "/user/set-username"
}

export const verifyApi = {
    getEmailCode: root + "/verify/mail",
    getImageCode: root + "/verify/image",
}

export const loverApi = {
    // /lover/exists/{fileId}
    exists: root + '/lover/exists/',
    // param : fileId
    love: root + '/lover/love',
    // param : fileId
    unloved: root + '/lover/unloved'
}