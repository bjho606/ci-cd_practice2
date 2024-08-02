import axios from 'axios'

const API_URL = '/api/v1'

export default {
  getContents(success, failure) {
    axios.get(`${API_URL}/contents`).then(success).catch(failure)
  },
  callNextContents(success, failure) {
    axios.get(`${API_URL}/contents/next`).then(success).catch(failure)
  },
  recallContents(success, failure) {
    axios.get(`{API_RUL}/contents/next`).then(success).catch(failure)
  }
}
