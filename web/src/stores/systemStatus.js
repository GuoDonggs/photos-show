import {reactive, ref} from 'vue'
import {defineStore} from 'pinia'

export const systemStore = defineStore('system', () => {
    const options = ref({
        isPc: window.innerWidth > 768,
    })

    window.addEventListener('resize', () => {
        options.value.isPc = window.innerWidth > 768
    })
    window.addEventListener('load', () => {
        options.value.isPc = window.innerWidth > 768
    })

    const cache = reactive({})

    return {options, cache}
})
