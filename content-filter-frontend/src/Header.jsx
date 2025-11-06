import React from 'react'
import { Link } from 'react-router-dom'

const Header = () => {
  return (
    <div>
      <h1>헤더입니다.</h1>
      <Link to={'/login'}>Login</Link>
      <Link to={'/Join'}>Join</Link>
    </div>
  )
}

export default Header