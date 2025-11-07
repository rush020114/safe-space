import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import Menu from './Menu'
import Login from './pages/auth/Login'
import { Route, Routes } from 'react-router-dom'
import ProtectedRoute from './components/auth/ProtectedRoute'
import ProtectedAdminRoute from './components/auth/ProtectedAdminRoute'
import Layout from './layouts/main/Layout'
import Join from './pages/auth/Join'

const App = () => {

  return (
    <>
      <Routes>
        <Route path='/' element={<Layout />}>
          <Route path='' element={<div>메인페이지</div>} />
          <Route path='/login' element={<Login />} />
          <Route path='/join' element={<Join />} />
          <Route path='/user' element={<ProtectedRoute><div>유저</div></ProtectedRoute>} />
          <Route path='/admin' element={<ProtectedAdminRoute><div>관리자</div></ProtectedAdminRoute>} />
        </Route>
      </Routes>
    </>
  )
}

export default App
