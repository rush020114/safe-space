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
import Post from './pages/post/Post'
import PostList from './pages/post/PostList'
import PostDetail from './pages/post/PostDetail'
import AdminReportList from './pages/admin/report/AdminReportList'

const App = () => {

  return (
    <>
      <Routes>
        <Route path='/' element={<Layout />}>
          <Route path='' element={<PostList />} />
          <Route path='/login' element={<Login />} />
          <Route path='/join' element={<Join />} />
          <Route path='/post' element={<ProtectedRoute><Post /></ProtectedRoute>} />
          <Route path='/post-detail/:postId' element={<PostDetail />} />
          <Route path='/user' element={<ProtectedRoute><div>유저</div></ProtectedRoute>} />
        </Route>
        <Route path='/admin' element={<Layout />}>
          <Route path='' element={<ProtectedAdminRoute><AdminReportList /></ProtectedAdminRoute>} />
        </Route>
      </Routes>
    </>
  )
}

export default App
