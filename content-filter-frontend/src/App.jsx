import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import Menu from './Menu'
import Login from './auth/Login'
import { Route, Routes } from 'react-router-dom'
import ProtectedRoute from './components/ProtectedRoute'
import ProtectedAdminRoute from './components/ProtectedAdminRoute'
import Header from './Header'

const App = () => {

  return (
    <>
      <Header />
      <Menu />

      <Routes>
        <Route path='' element={<div>메인페이지</div>} />
        <Route path='/login' element={<Login />} />
        <Route path='/user' element={<ProtectedRoute><div>유저</div></ProtectedRoute>} />
        <Route path='/admin' element={<ProtectedAdminRoute><div>관리자</div></ProtectedAdminRoute>} />
      </Routes>
    </>
  )
}

export default App
