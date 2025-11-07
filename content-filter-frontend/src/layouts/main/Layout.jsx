import React from 'react';
import { Outlet } from 'react-router-dom';
import { Container } from 'react-bootstrap';
import Header from './Header';

const Layout = () => {
  return (
    <>
      <Header />
      <Container className="mt-4">
        <Outlet />
      </Container>
    </>
  );
};

export default Layout;