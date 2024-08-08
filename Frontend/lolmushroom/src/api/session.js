import axios from 'axios'
const API_URL = '/api/v1'

export default {
  createSession(contentsOrder, success, failure) {
    console.log(contentsOrder)
    return axios.post(`${API_URL}/sessions`, contentsOrder).then(success).catch(failure)
  },
  createSubSession(sessionId, success, failure) {
    return axios.post(`${API_URL}/sessions/${sessionId}`).then(success).catch(failure)
  },
  getSessionInfo(sessionId, success, failure) {
    return axios.get(`${API_URL}/sessions/${sessionId}`).then(success).catch(failure)
  },
  getSubSessionInfo(sessionId, subSessionId, success, failure) {
    return axios
      .get(`${API_URL}/sessions/${sessionId}/${subSessionId}`)
      .then(success)
      .catch(failure)
  },
  getSessionConnection(sessionId, userName, success, failure) {
    return axios
      .post(`${API_URL}/sessions/${sessionId}/connections`, userName)
      .then(success)
      .catch(failure)
  },
  changeUserName(userName, success, failure) {
    return axios.patch(`${API_URL}/users/usernmae`, userName).then(success).catch(failure)
  },
  changeSubSessionName(subSessionId, groupName, success, failure) {
    return axios
      .patch(`${API_URL}/sessions/${subSessionId}/group-name`, groupName)
      .then(success)
      .catch(failure)
  },
  getSubSessionReady(subSessionId, success, failure) {
    return axios.get(`${API_URL}/sessions/${subSessionId}/ready`).then(success).catch(failure)
  },
  getSubSessionQuit(subSessionId, success, failure) {
    return axios.get(`${API_URL}/sessions/${subSessionId}/quit`).then(success).catch(failure)
  }
}
