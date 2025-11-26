import { useEffect, useRef, useState } from 'react'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'
import { API } from '../config/api'

export default function Chat() {
  const [messages, setMessages] = useState([])
  const [input, setInput] = useState('')
  const clientRef = useRef(null)

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS(`${API.BASE_URL}/ws`),
      reconnectDelay: 5000
    })
    client.onConnect = () => {
      client.subscribe('/topic/chat', msg => {
        try {
          const body = JSON.parse(msg.body)
          setMessages(m => [...m, body.content || body.message || ''])
        } catch {
          setMessages(m => [...m, msg.body])
        }
      })
      client.publish({ destination: '/app/chat.addUser', body: JSON.stringify({}) })
    }
    client.activate()
    clientRef.current = client
    return () => client.deactivate()
  }, [])

  const send = () => {
    const c = clientRef.current
    if (!c || !input.trim()) return
    c.publish({ destination: '/app/chat.sendMessage', body: JSON.stringify({ content: input }) })
    setInput('')
  }

  return (
    <div style={{ padding: 20 }}>
      <h2>Chat</h2>
      <div style={{ border: '1px solid #ddd', height: 240, overflow: 'auto', padding: 8, marginBottom: 8 }}>
        {messages.map((m, i) => (<div key={i}>{m}</div>))}
      </div>
      <div>
        <input value={input} onChange={e => setInput(e.target.value)} style={{ width: '70%', padding: 8 }} />
        <button onClick={send} style={{ padding: 8, marginLeft: 8 }}>Gửi</button>
      </div>
    </div>
  )
}

