import "./App.css";
import styled from "styled-components";
import { BrowserRouter as Router, Switch, Route } from "react-router-dom"; // 히스토리 모드 제거
import Header from "./components/common/Header";
import Sidebar from "./components/common/Sidebar";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Team from "./pages/Team";

function App() {
  return (
    <div className="App">
      <Header />
      <Router>
        <Sidebar />
        <Center>
          <Switch>
            <Route path="/" exact={true} component={Home} />
            <Route path="/login" exact={true} component={Login} />
            <Route path="/team/:id" exact={true} component={Team} />
          </Switch>
        </Center>
      </Router>
    </div>
  );
}

const Center = styled.div`
  position: absolute;
  top: 65px;
  left: 200px;
  display: flex;
  flex-direction: row;
  width: calc(100% - 200px);
`;

export default App;
