import { Client } from '@stomp/stompjs'

const { VITE_API_WEBSOCKET_URL } = import.meta.env

let stompClient = null

const connect = (sessionId, onMessageReceived, onConnect, onError) => {
  stompClient = new Client({
    brokerURL: VITE_API_WEBSOCKET_URL,
    reconnectDelay: 5000,
    onConnect: (frame) => {
      console.log('Connected: ' + frame)
      stompClient.subscribe(`/subscribe/chat/session/${sessionId}`, (message) => {
        console.log('Received message:', message.body) // 추가된 로그
        onMessageReceived(JSON.parse(message.body))
      })
      if (onConnect) {
        onConnect(frame)
      }
    },
    onStompError: (frame) => {
      console.error('Broker reported error: ' + frame.headers['message'])
      console.error('Additional Details: ' + frame.body)
      if (onError) {
        onError(frame)
      }
    },
    onWebSocketClose: (error) => {
      console.error('WebSocket closed: ', error)
      if (onError) {
        onError(error)
      }
    },
    onWebSocketError: (error) => {
      console.error('WebSocket error: ', error)
      if (onError) {
        onError(error)
      }
    }
  })

  stompClient.activate()
}

const disconnect = () => {
  if (stompClient !== null) {
    stompClient.deactivate(() => {
      console.log('Disconnected')
    })
  }
}

const sendMessage = (destination, message) => {
  if (stompClient && stompClient.connected) {
    stompClient.publish({
      destination: destination,
      body: JSON.stringify(message)
    })
    console.log('Sent message:', message)
  } else {
    console.error('WebSocket is not connected')
  }
}

export default {
  connect,
  disconnect,
  sendMessage
}
