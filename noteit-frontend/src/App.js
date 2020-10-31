import React from "react";
import {BrowserRouter as Router, Route, Switch} from "react-router-dom";
import {Dashboard} from "./home";
import {Header, theme} from "./layout";
import Login from "./auth/Login";
import {ThemeProvider} from '@material-ui/core/styles';

function App() {
    return (
        <ThemeProvider theme={theme}>
            <Router>
                <>
                    <Header/>
                    <hr/>
                    <Switch>
                        <Route exact path="/login">
                            <Login/>
                        </Route>
                        <Route exact path="/">
                            <Dashboard/>
                        </Route>
                    </Switch>
                </>
            </Router>
        </ThemeProvider>
    );
}

export default App;
