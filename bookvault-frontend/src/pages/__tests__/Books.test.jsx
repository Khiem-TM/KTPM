import { render, screen } from '@testing-library/react'
jest.mock('../../services/bookService', () => ({ default: { getBooks: jest.fn(async () => []) } }))
import Books from '../Books'

test('renders books page header', async () => {
  render(<Books />)
  expect(await screen.findByText(/Danh sách sách/i)).toBeInTheDocument()
})

