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
  onError
}) => {
  // stompClient가 이미 존재하고 연결된 상태라면 새로운 클라이언트를 생성하지 않음
  if (stompClient && stompClient.connected) {
    console.log('Already connected, adding new subscriptions')

    // 기존 연결 상태에서 새 구독 추가
    addSubscriptions(sessionId, contentsName, onMessageReceived, onEventReceived)

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
      addSubscriptions(sessionId, contentsName, onMessageReceived, onEventReceived)

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

const addSubscriptions = (sessionId, contentsName, onMessageReceived, onEventReceived) => {
  /**
   * IMP 1. Chatting을 위한 Socket 연결
   * * => 여러 Session을 연결할 수 있지만, Main과 1개의 SubSession에 대한 Socket 연결만 함
   */
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

  /**
   * IMP 2. MeshRoom의 Room 생성, Room 입장에 대한 Event에 대한 Socket
   * * Main과 SubSession들에 대한 실시간 정보에 대한 SubScription
   */
  const progressKey = `progress`
  if (!subscriptionMap.has(progressKey)) {
    const progressSubscription = stompClient.subscribe(
      `/subscribe/progress/${sessionId}`,
      (message) => {
        console.log('Received event from progress Subscribe:', message.body)
        if (onEventReceived) {
          onEventReceived(message)
        }
      }
    )
    subscriptionMap.set(progressKey, progressSubscription)
  }

  /**
   * IMP 3. MeshRoom 진행자의 Contents의 흐름제어 Event에 대한 Socket
   * * 현재 Contents에 대한 정보를 전체 참여자들에게 반환한다.
   */
  const contentsKey = 'contents'
  if (!subscriptionMap.has(contentsKey)) {
    const contentsSubscription = stompClient.subscribe(
      `/subscribe/contents/${sessionId}`,
      (message) => {
        console.log('Received event from Contents Subscribe', message.body)
        if (onEventReceived) {
          onEventReceived(message)
        }
      }
    )
    subscriptionMap.set(contentsKey, contentsSubscription)
  }

  /**
   * IMP 4. MeshRoom 참여자가 각 Contents Data에 대한 Subscribe
   */
  const gameKey = `game-${contentsName}`
  if (!subscriptionMap.has(gameKey)) {
    const gameSubscription = stompClient.subscribe(`/subscribe/game/${contentsName}`, (event) => {
      console.log(`Received event from ${contentsName} Subscribe`)
      if (onEventReceived) {
        onEventReceived(JSON.parse(event.body))
      }
    })
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
