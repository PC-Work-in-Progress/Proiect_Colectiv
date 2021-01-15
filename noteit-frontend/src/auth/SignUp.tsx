import {
    IonButton,
    IonCard,
    IonCardHeader,
    IonContent,
    IonHeader,
    IonInput,
    IonLoading,
    IonPage,
    IonTitle,
    IonToolbar,
    IonIcon,
    IonModal,
    IonAlert
} from "@ionic/react";
import React, {useState} from "react";
import {useContext} from "react"
import {Link, Redirect, RouteComponentProps} from "react-router-dom";
import {SignUpContext} from "./AuthProvider";
import "./Auth.css"
import {Header} from "../layout/Header";
import {information, informationCircleOutline} from "ionicons/icons";

interface SignUpState {
    email?: string;
    full_name?: string;
    username?: string;
    password?: string;
}

export const SignUp: React.FC<RouteComponentProps> = ({history}) => {
    const {isSigned, isSigning, signup, signupError, message1} = useContext(SignUpContext);
    const [state, setState] = useState<SignUpState>({});
    const {email, full_name, username, password} = state;
    const [errorUsername, setErrorUsername] = useState('');
    const [errorEmail, setErrorEmail] = useState('');
    const [errorFullName, setErrorFullName] = useState('');
    const [errorPassword, setErrorPassword] = useState('');
    const [modal, setShowModal] = useState(false);

    const handleSignUp = () => {
        setErrorUsername('');
        setErrorFullName('');
        setErrorEmail('');
        setErrorPassword('');
        validateUsername(username || '');
        validateEmail(email || '');
        validateFullName(full_name || '');
        validatePassword(password || '');
        if (errorEmail.length === 0 && errorFullName.length === 0 && errorPassword.length === 0 && errorUsername.length === 0) {
            signup?.(username, password, full_name, email);
        }
    };
    if (isSigned) {
        return <Redirect to={{pathname: '/login'}}/>
    }


    function validateUsername(username: string): void {
        if (username === '') {
            setErrorUsername('Username must not be empty');
        } else if (message1?.includes('Username') || message1?.includes('username')) {
            setErrorUsername(message1);
        }
    }

    function validatePassword(password: string): void {
        if (password.length >= 16) {
            setErrorPassword('');
        } else if (password.length >= 8) {
            const strongRegex = new RegExp("^(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*])");
            if (strongRegex.test(password)) {
                setErrorPassword('');
            }
        } else {
            setErrorPassword('Password is not strong enough');
        }
    }

    function validateEmail(email: string): void {
        if (email === '') {
            setErrorEmail('Email must not be empty');
        } else if (email.indexOf('@') === -1) {
            setErrorEmail('Email must contain \'@\'');
        }
    }

    function validateFullName(fullName: string): void {
        if (fullName === '') {
            setErrorFullName('Full-name must not be empty');
        } else if (fullName.indexOf(' ') === -1) {
            setErrorFullName('Full-name must contains at least 2 names. eg John Doe');
        }
    }

    return (
        <IonPage>
            <IonContent>
                <Header/>
                <div className="page">
                    <div className="login-div">
                        <IonCard>
                            <IonCardHeader>
                                <h5> Sign Up </h5>
                            </IonCardHeader>
                            <div className="form">
                                <IonInput placeholder="Username" value={username}
                                          onIonChange={e => setState({...state, username: e.detail.value || ''})}/>
                                {errorUsername && <div>{errorUsername}</div>}
                                {errorUsername === '' && (message1?.includes('Username') || message1?.includes('username'))
                                && (<div> {message1} </div>)}

                                <IonInput placeholder="Email" value={email}
                                          onIonChange={e => setState({...state, email: e.detail.value || ''})}/>
                                {errorEmail && <div>{errorEmail}</div>}
                                {errorEmail === '' && (message1?.includes('Email') || message1?.includes('email'))
                                && (<div> {message1} </div>)}

                                <IonInput placeholder="Full Name" value={full_name}
                                          onIonChange={e => setState({...state, full_name: e.detail.value || ''})}/>
                                {errorFullName && <div>{errorFullName}</div>}
                                {errorFullName === '' && message1?.includes(' name')
                                && (<div> {message1} </div>)}

                                <IonInput type="password" placeholder="Password" value={password}
                                          onIonChange={e => setState({...state, password: e.detail.value || ''})}/>
                                {errorPassword && (
                                    <div>{errorPassword}
                                        <IonIcon icon={informationCircleOutline} onClick={e => setShowModal(true)}/>
                                    </div>)}
                                {modal && (
                                    <IonAlert
                                        isOpen={modal}
                                        onDidDismiss={() => setShowModal(false)}
                                        cssClass='modal'
                                        message={'Make sure it\'s at least 16 characters OR 8 including a number, a lowercase\n' +
                                        '                                            letter and a special character'}
                                        buttons={['OK']}
                                    />
                                )}
                                {errorPassword === '' && message1?.includes('password')
                                && (<div> {message1} </div>)}

                                <IonLoading isOpen={isSigning}/>
                                {signupError && (
                                    <div>{'Failed to sign up'}</div>)}
                                <IonButton onClick={handleSignUp}>Sign Up</IonButton>
                                <br></br>
                                <br></br>
                                <Link to="/login">I already have an account</Link>
                            </div>
                        </IonCard>
                    </div>
                </div>
            </IonContent>
        </IonPage>
    )
}   
