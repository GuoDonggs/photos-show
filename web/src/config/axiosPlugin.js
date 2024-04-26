import axios from "axios";


export default {
    getAxios(token) {
        if (token === undefined || token === null || token.length < 3) {
            token = localStorage.getItem("token")
        }
        return axios.create({
            headers: {
                'Auth-Token': token
            }
        })
    }
}