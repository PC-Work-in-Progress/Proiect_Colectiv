import React, {useContext, useState} from "react";
import {IonButton, IonCard, IonCardHeader, IonContent, IonHeader, IonInput, IonLoading, IonPage, IonTitle, IonToolbar} from '@ionic/react';
import {AuthContext} from "./AuthProvider";
import {Link, Redirect, RouteComponentProps} from "react-router-dom";
import "./Auth.css"
import { Header } from "../layout/Header";

interface LoginState {
    username?: string;
    password?: string;
  }

export const Login: React.FC<RouteComponentProps> = ({ history }) =>{
    const [state, setState] = useState<LoginState>({});
    const { username, password } = state;
    const { isAuthenticated, isAuthenticating, login, authenticationError } = useContext(AuthContext);
    console.log("Render login");
    const handleLogin = () => {
        login?.(username, password);
      };
    if (isAuthenticated) {
        return <Redirect to={{ pathname: '/home' }} />
    }
    return (
        <IonPage>
            <IonContent>
                <Header/>
                <div className = "page">
                <div className ="login-div">
                    <IonCard>
                        <IonCardHeader>
                           <h5> Log in </h5>
                        </IonCardHeader>
                        <div className="form">
                        <IonInput placeholder="Username" value={username}
                        onIonChange={e => setState({...state,username: e.detail.value || ''})}/>
                        
                        <IonInput type="password" placeholder="Password" value={password}
                        onIonChange={e => setState({...state,password: e.detail.value || ''})}/>
                        
                        <IonLoading isOpen={isAuthenticating}/> 
                            {authenticationError && (
                                <div>{'Wrong username or password'}</div>
                            )}
                        
                        <IonButton onClick={handleLogin}>Login</IonButton>
                        <br></br>
                        <IonButton href = '/signup'>Signup</IonButton>
                        </div>

                    </IonCard>
                </div>
                </div>
            </IonContent>
            </IonPage>
    );
}

