import { render, screen } from '@testing-library/react'
import App from './App'

test('renders navbar brand', () => {
  render(<App />)
  const brands = screen.getAllByText(/BookVault/i)
  expect(brands.length).toBeGreaterThan(0)
})
