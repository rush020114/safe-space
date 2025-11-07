import React from 'react'
import { useSelector } from 'react-redux'
import { Link } from 'react-router-dom';
import { isAdmin, isAuthenticated } from './apis/authCheck';

const Menu = () => {

  // 구독하고 있는 store 데이터가 변경되면 재렌더링됨.
  const token = useSelector(state => state.auth.token);
  
  return (
    <div style={{display: 'flex', gap: '30px', marginBottom: '30px'}}>
      <div><Link to={''}>메인화면</Link></div>
      <div><Link to={'/anyone'}>아무나화면</Link></div>

      {isAuthenticated(token) && <div><Link to={'/user'}>유저만</Link></div>}
      {isAdmin(token) && <div><Link to={'/admin'}>관리자만</Link></div>}
    </div>
  )
}

export default Menu