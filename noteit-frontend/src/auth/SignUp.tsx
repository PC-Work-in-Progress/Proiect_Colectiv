import { IonButton, IonContent, IonHeader, IonInput, IonLoading, IonPage, IonTitle, IonToolbar } from "@ionic/react";
import React, { useState } from "react";
import { useContext } from "react"
import { Redirect, RouteComponentProps } from "react-router-dom";
import { SignUpContext } from "./AuthProvider";
interface SignUpState {
    email?: string;
    fullName?: string;
    username?: string;
    password?: string;
}
export const SignUp: React.FC<RouteComponentProps> = ({ history }) => {
    const {isSigned, isSigning, signup, signupError } = useContext(SignUpContext);
    const [state, setState] = useState<SignUpState>({});
    const {email, fullName, username, password} = state;
    const handleSignUp = () => {
        signup?.(username,password,fullName,email);
    };
    if(isSigned) {
        return <Redirect to={{ pathname: '/login'}} />
    }
    return (
        <IonPage>
            <IonHeader>
                <IonToolbar>
                    <IonTitle>Sign Up</IonTitle>
                </IonToolbar>
            </IonHeader>
            <IonContent>
                <IonInput placeholder="Username" value={username} 
                    onIonChange={e => setState({...state, username: e.detail.value || ''})}/>
                <IonInput placeholder="Email" value={email} 
                    onIonChange={e => setState({...state, email: e.detail.value || ''})}/>
                <IonInput placeholder="Full Name" value={fullName} 
                    onIonChange={e => setState({...state, fullName: e.detail.value || ''})}/>
                <IonInput type="password" placeholder="Password" value={password}
                    onIonChange={e => setState({...state, password: e.detail.value || ''})}/>
                <IonLoading isOpen={isSigning}/>
                    {signupError && (
                        <div>{signupError.message || 'Failed to sign up'}</div>)}
        <IonButton onClick={handleSignUp}>Sign Up</IonButton>
      </IonContent>
    </IonPage>
    )     
}   
