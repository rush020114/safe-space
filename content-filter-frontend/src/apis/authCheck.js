import { jwtDecode } from "jwt-decode"

// 토큰 만료 => 리턴 true
export const isTokenExpired = token => {
  const decodedToken = jwtDecode(token);
  const currentTime = Date.now() / 1000;

  return decodedToken.exp < currentTime;
}

// 토큰 존재 + 만료되지 않음 => 리턴 true
export const isAuthenticated = token => {
  if(!token) return false;

  if(isTokenExpired(token)){
    localStorage.removeItem('accessToken');
    return false;
  }

  return true;
}

// 토큰 존재 + 만료되지 않음 + 관리자 권한 => 리턴 true
export const isAdmin = token => {
  if(!isAuthenticated(token)) return false;

  const decodedToken = jwtDecode(token);

  return decodedToken.role === 'ROLE_ADMIN'
}