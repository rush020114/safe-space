import { Navbar, Container, Nav } from 'react-bootstrap';

const Header = () => {
  return (
    <Navbar bg="light" data-bs-theme="light">
      <Container>
        <Navbar.Brand href="/">safespace</Navbar.Brand>
        <Nav className="me-auto">
          <Nav.Link href="/login">로그인</Nav.Link>
          <Nav.Link href="/join">회원가입</Nav.Link>
        </Nav>
      </Container>
    </Navbar>
  );
};

export default Header;