import { useState } from 'react'
import borrowingService from '../services/borrowingService'

export default function Borrowed() {
  const [loanId, setLoanId] = useState('')
  const [msg, setMsg] = useState('')
  const returnLoan = async () => {
    try {
      setMsg('')
      await borrowingService.returnLoan(Number(loanId))
      setMsg('Đã trả sách')
      setLoanId('')
    } catch {
      setMsg('Trả sách thất bại')
    }
  }
  return (
    <div style={{ padding: 20 }}>
      <h2>Sách đã mượn</h2>
      <div style={{ marginTop: 12 }}>
        <input value={loanId} onChange={e => setLoanId(e.target.value)} placeholder="Loan ID" style={{ padding: 8, marginRight: 8 }} />
        <button onClick={returnLoan}>Trả sách</button>
      </div>
      {msg && <div style={{ marginTop: 8 }}>{msg}</div>}
    </div>
  )
}
