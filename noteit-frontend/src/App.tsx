import React from 'react';
import {Redirect, Route} from 'react-router-dom';
import {IonApp, IonRouterOutlet} from '@ionic/react';
import {IonReactRouter} from '@ionic/react-router';

/* Core CSS required for Ionic components to work properly */
import '@ionic/react/css/core.css';

/* Basic CSS for apps built with Ionic */
import '@ionic/react/css/normalize.css';
import '@ionic/react/css/structure.css';
import '@ionic/react/css/typography.css';

/* Optional CSS utils that can be commented out */
import '@ionic/react/css/padding.css';
import '@ionic/react/css/float-elements.css';
import '@ionic/react/css/text-alignment.css';
import '@ionic/react/css/text-transformation.css';
import '@ionic/react/css/flex-utils.css';
import '@ionic/react/css/display.css';

/* Theme variables */
import './theme/variables.css';
import {Login} from "./auth";
import {Home} from "./home";
import {SignUp} from './auth/SignUp';
import {AuthProvider} from './auth/AuthProvider';
import {RoomPage} from './roomPage/Room';
import {File} from './file';
import {User} from "./user";
import {Discover} from "./discover";

const App: React.FC = () => (
    <IonApp>
        <IonReactRouter>
            <IonRouterOutlet>
                <AuthProvider>
                    <Route path="/login" component={Login} exact={true}/>
                    <Route path="/signup" component={SignUp} exact={true}/>
                    <Route path="/home" component={Home} exact={true}/>
                    <Route path="/room/:id/:fileId" component={File} exact={true}/>
                    <Route path='/room/:id' component={RoomPage} exact={true}/>
                    <Route path='/user' component={User} exact={true}/>
                    <Route path='/discover' component={Discover} exact={true}/>
                    <Route exact path="/" render={() => <Redirect to="/login"/>}/>
                </AuthProvider>
            </IonRouterOutlet>
        </IonReactRouter>
    </IonApp>
);

export default App;