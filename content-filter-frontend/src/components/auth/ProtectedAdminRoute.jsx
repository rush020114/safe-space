import React, { useEffect, useState } from 'react'
import { useSelector } from 'react-redux'
import { isAdmin } from '../../apis/authCheck';
import { Navigate } from 'react-router-dom';

const ProtectedAdminRoute = ({children}) => {
  const token = useSelector(state => state.auth.token);
  const [isAccessible, setIsAccessible] = useState(null);
  
  useEffect(() => {
    if(!isAdmin(token)){
      alert('로그인이 필요합니다. \n 첫페이지로 이동합니다.')
      setIsAccessible(false);
    } else {
      setIsAccessible(true);
    }
  }, []);

  if(isAccessible === null) return null;

  return isAccessible ? children : <Navigate to={'/'} />;
  
}

export default ProtectedAdminRoute