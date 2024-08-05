import { Client } from '@stomp/stompjs'
const { VITE_API_WEBSOCKET_URL } = import.meta.env

let stompClient = null
let subscriptionMap = new Map() // 구독을 관리할 Map 객체

const connect = ({
  sessionId,
  contentsName,
  onMessageReceived,
  onEventReceived,
  onConnect,
  onError,
  subscriptions // 추가된 파라미터: 구독할 리스트
}) => {
  // stompClient가 이미 존재하고 연결된 상태라면 새로운 클라이언트를 생성하지 않음
  if (stompClient && stompClient.connected) {
    console.log('Already connected, adding new subscriptions')

    // 기존 연결 상태에서 새 구독 추가
    addSubscriptions(sessionId, contentsName, onMessageReceived, onEventReceived, subscriptions)

    if (onConnect) {
      onConnect('Already connected')
    }
    return
  }

  stompClient = new Client({
    brokerURL: VITE_API_WEBSOCKET_URL,
    reconnectDelay: 5000,
    onConnect: (frame) => {
      console.log('Connected: ' + frame)

      // 구독 설정 및 Join 메시지 전송 (필요한 경우에만)
      addSubscriptions(sessionId, contentsName, onMessageReceived, onEventReceived, subscriptions)

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

const addSubscriptions = (
  sessionId,
  contentsName,
  onMessageReceived,
  onEventReceived,
  subscriptions
) => {
  if (!subscriptions || subscriptions.length === 0) {
    console.warn('No subscriptions provided')
    return
  }

  // 구독을 선택적으로 추가
  subscriptions.forEach((subscription) => {
    switch (subscription) {
      case 'chat':
        addChatSubscription(sessionId, onMessageReceived)
        break
      case 'session':
        addSessionSubscription(sessionId, onEventReceived)
        break
      case 'progress':
        addContentsSubscription(sessionId, onEventReceived)
        break
      case 'game':
        addGameSubscription(sessionId, contentsName, onEventReceived)
        break
      default:
        console.warn(`Unknown subscription type: ${subscription}`)
    }
  })
}

const addChatSubscription = (sessionId, onMessageReceived) => {
  const sessionKey = `session-${sessionId}`
  if (!subscriptionMap.has(sessionKey)) {
    const sessionSubscription = stompClient.subscribe(
      `/subscribe/chat/session/${sessionId}`,
      (message) => {
        console.log(`Received message from session ${sessionId}:`, message.body)
        if (onMessageReceived) {
          onMessageReceived(JSON.parse(message.body), sessionId) // sessionId를 함께 전달
        }
      }
    )
    subscriptionMap.set(sessionKey, sessionSubscription)

    // 처음 구독할 때만 Join 메시지를 전송
    const joinMessage = {
      sessionId: sessionId,
      content: '',
      timestamp: new Date().toISOString()
    }
    stompClient.publish({
      destination: `/publish/chat/join`,
      body: JSON.stringify(joinMessage)
    })
  }
}

const addSessionSubscription = (sessionId, onEventReceived) => {
  const sessionKey = `session`
  if (!subscriptionMap.has(sessionKey)) {
    const sessionSubscription = stompClient.subscribe(
      `/subscribe/sessions/${sessionId}`,
      (message) => {
        console.log('Received event from session Subscribe:', message.body)
        if (onEventReceived) {
          onEventReceived(JSON.parse(message.body))
        }
      }
    )
    subscriptionMap.set(sessionKey, sessionSubscription)
  }
}

const addContentsSubscription = (sessionId, onEventReceived) => {
  const contentsKey = 'contents'
  if (!subscriptionMap.has(contentsKey)) {
    const contentsSubscription = stompClient.subscribe(
      `/subscribe/contents/${sessionId}`,
      (message) => {
        console.log('Received event from Contents Subscribe', message.body)
        if (onEventReceived) {
          onEventReceived(JSON.parse(message.body))
        }
      }
    )
    subscriptionMap.set(contentsKey, contentsSubscription)
  }
}

const addGameSubscription = (sessionId, contentsName, onEventReceived) => {
  const gameKey = `game-${contentsName}`
  if (!subscriptionMap.has(gameKey)) {
    const gameSubscription = stompClient.subscribe(
      `/subscribe/game/${contentsName}/${sessionId}`,
      (event) => {
        console.log(`Received event from ${contentsName} Subscribe`)
        if (onEventReceived) {
          onEventReceived(JSON.parse(event.body))
        }
      }
    )
    console.log('Mushroom Grow Game에 입장했습니다.')
    subscriptionMap.set(gameKey, gameSubscription)
  }
}

const unsubscribe = (sessionId) => {
  const sessionKey = `session-${sessionId}`
  if (subscriptionMap.has(sessionKey)) {
    const subscription = subscriptionMap.get(sessionKey)
    subscription.unsubscribe()
    console.log(`Unsubscribed from session ${sessionId}`)
    subscriptionMap.delete(sessionKey) // Map에서 해당 구독 삭제
  } else {
    console.error(`No subscription found for session ${sessionId}`)
  }
}

const disconnect = () => {
  if (stompClient !== null) {
    stompClient.deactivate(() => {
      console.log('Disconnected')
    })
  }
}
/**
 * IMP 1. Destination에 따라 Message를 보내는 Code
 * @param {*} destination
 * @param {*} message
 */
const sendMessage = (destination, message) => {
  if (stompClient && stompClient.connected) {
    stompClient.publish({
      destination: destination,
      body: JSON.stringify(message)
    })
    console.log('Sent message:', message)
  } else {
    console.error('WebSocket is not Connected')
  }
}
/**
 * IMP 2. 버섯 키우기 Game 진행 중에, Click Data를 보내는 Code
 * @param {*} destination
 * @param {*} message
 */
const sendClickData = (destination, click) => {
  if (stompClient && stompClient.connected) {
    stompClient.publish({
      destination: destination,
      body: JSON.stringify(click)
    })
  } else {
    console.error('WebSocket is not Connected')
  }
}

export default {
  connect,
  disconnect,
  sendMessage,
  sendClickData,
  unsubscribe
}
