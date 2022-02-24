import './App.scss';
import 'bootstrap/dist/css/bootstrap.min.css';
import {Container, FormText, Navbar, Spinner} from "react-bootstrap";
import useFetch from "react-fetch-hook";

function App() {
    const { isLoading, data, error } = useFetch(
        "http://localhost:8080/restaurants"
    );

  return (
    <div className="App">
      <Container>
          <Navbar bg="light">
              <Container>
                  <Navbar.Brand>
                      <img
                          src="https://img.icons8.com/emoji/48/000000/hamburger-emoji.png"
                          width="30"
                          height="30"
                          className="d-inline-block align-top"
                          alt="Bekkaway logo"
                      />{' '}
                      Bekkaway</Navbar.Brand>
                  {isLoading && <Navbar.Text>
                      <Spinner animation="border" role="status">
                          <span className="visually-hidden">Loading...</span>
                      </Spinner>
                  </Navbar.Text>}
                  {data && <FormText>{data}</FormText>}
              </Container>
          </Navbar>


      </Container>
    </div>
  );
}

export default App;
