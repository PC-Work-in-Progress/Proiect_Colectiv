import React, {useContext, useState} from "react";
import {IonButton, IonContent, IonHeader, IonInput, IonLoading, IonPage, IonTitle, IonToolbar} from '@ionic/react';
import {AuthContext} from "./AuthProvider";
import {Link, Redirect, RouteComponentProps} from "react-router-dom";


interface LoginState {
    username?: string;
    password?: string;
  }

export const Login: React.FC<RouteComponentProps> = ({ history }) =>{
    const [state, setState] = useState<LoginState>({});
    const { username, password } = state;
    const { isAuthenticated, isAuthenticating, login, authenticationError } = useContext(AuthContext);
    {/*
    let [text, setText] = useState('');
    useEffect(() => {
        const fetchMessage = async () => {
            let s = await getMessage();
            setText(s);
        };
        fetchMessage();
    }, []);
    */}
    const handleLogin = () => {
        login?.(username, password);
      };
    if (isAuthenticated) {
        return <Redirect to={{ pathname: '/' }} />
    }
    return (
        <IonPage>
            <IonHeader>
                <IonToolbar>
                    <IonTitle>Welcome to NoteIT - LogIn</IonTitle>
                </IonToolbar>
            </IonHeader>
            <IonContent>
                {/*<IonLabel>Message: {text}</IonLabel>*/}
                <IonInput placeholder="Username" value={username}
                onIonChange={e => setState({...state,username: e.detail.value || ''})}/>
                <IonInput type="password" placeholder="Password" value={password}
                onIonChange={e => setState({...state,password: e.detail.value || ''})}/>
                <IonLoading isOpen={isAuthenticating}/> 
                    {authenticationError && (
                        <div>{authenticationError.message || 'Failed to authenticate'}</div>
                     )}
                <IonButton onClick={handleLogin}>Login</IonButton>
                <div><br></br> </div>
                <div><Link to="/signup">Don't have an account.Click here to sign up! :)</Link></div>
            </IonContent>
        </IonPage>
    );
}

