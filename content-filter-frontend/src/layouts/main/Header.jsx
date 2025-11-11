import { Navbar, Container, Nav, Button, NavDropdown } from 'react-bootstrap';
import { useDispatch, useSelector } from 'react-redux';
import { Link, useNavigate } from 'react-router-dom';
import { isAdmin, isAuthenticated } from '../../apis/authCheck';
import { logoutReducer } from '../../redux/authSlice';
import { jwtDecode } from 'jwt-decode';

const Header = () => {
  const token = useSelector(state => state.auth.token);
  const dispatch = useDispatch();
  const username = token ? jwtDecode(token).sub : null
  const nav = useNavigate();

  return (
    <Navbar bg="light" data-bs-theme="light">
      <Container>
        <Navbar.Brand href="/">SAFE SPACE</Navbar.Brand>
        <Nav className="ms-auto align-items-center">
          {
            isAuthenticated(token)
            ? (
              <Nav.Item>
                <div className='d-flex align-items-center gap-3'>
                  <Navbar.Text className="me-3">
                    {username}님 환영합니다!
                  </Navbar.Text>
                  <NavDropdown title="메뉴" id="user-menu-dropdown" align="end">
                    <NavDropdown.Item as={Link} to="">내 정보</NavDropdown.Item>
                    <NavDropdown.Item as={Link} to="/post">글쓰기</NavDropdown.Item>
                    {
                      isAdmin(token)
                      &&
                      <NavDropdown.Item as={Link} to="">운영 관리</NavDropdown.Item>
                    }
                    <NavDropdown.Divider />
                    <NavDropdown.Item 
                      onClick={() => {
                        dispatch(logoutReducer());
                        nav('/')
                      }}
                      className="text-danger"
                    >
                      로그아웃
                    </NavDropdown.Item>
                  </NavDropdown>
                </div>
              </Nav.Item>
            )
            : (
              <>
                <Nav.Link as={Link} to="/login">로그인</Nav.Link>
                <Nav.Link as={Link} to="/join">회원가입</Nav.Link>
              </>
            )
          }
        </Nav>
      </Container>
    </Navbar>
  );
};

export default Header;