import {RouteComponentProps} from "react-router";
import {
    IonButton,
    IonCard,
    IonCardContent,
    IonCardHeader,
    IonCardTitle,
    IonCol,
    IonContent, IonGrid, IonIcon, IonInput, IonItem, IonLabel, IonList,
    IonLoading, IonNote,
    IonRow, IonToggle
} from "@ionic/react";
import React, {useContext, useEffect, useState} from "react";

import "./User.css"
import {useUserSettings} from "./useUserSettings";
import {Header} from "../layout/Header";
import {AuthContext} from "../auth/AuthProvider";

export const User: React.FC<RouteComponentProps> = ({history}) => {
    const {logout} = useContext(AuthContext);

    const {state, passwordUpdate, usernameUpdate, fullnameUpdate, emailUpdate} = useUserSettings();
    const {user} = state;
    const [profile, setProfile] = useState(true);
    const [accountSecurity, setAccountSecurity] = useState(false);

    const [password, setPassword] = useState('');
    const [confirmedPassword, setConfirmedPassword] = useState('');
    const [errorPassword, setErrorPassword] = useState('');

    const [username, setUsername] = useState(user.userName);
    const [fullname, setFullname] = useState('');
    const [email, setEmail] = useState('');
    const [errorEmail, setErrorEmail] = useState('');

    useEffect(() => {
        setProfile(true);
    }, []);

    useEffect(() => {
        setUsername(user.userName);
        console.log(username);
        setFullname(user.fullName);
        setEmail(user.email);
    }, [user]);


    function handleLogout(): void {
        logout?.();
        history.push('/login');
        history.go(0);
    }

    function validatePassword(password: string): void {
        if (password.length >= 16) {
            setErrorPassword('');
        } else if (password.length >= 8) {
            const specialCh = ['.', '']
            const strongRegex = new RegExp("^(?=.*[a-z])(?=.*[0-9])(?=.*[!@#\$%\^&\*])");
            if (strongRegex.test(password)) {
                setErrorPassword('');
            }
        } else {
            setErrorPassword('Password is not strong enough');
        }
    }

    return (
        <IonContent>
            <div className='flex-page'>
                <Header/>
                <IonGrid class="grid-page ion-padding-bottom">
                    <IonRow class="fullscreen">
                        <IonCol class="fullscreen" size="3.5">
                            <div className="first-col">
                                <IonCard class="rooms">
                                    <IonCardHeader>
                                        <IonCardTitle>Profile Settings</IonCardTitle>
                                    </IonCardHeader>
                                    <IonCardContent>
                                        <IonItem button onClick={() => {
                                            setProfile(true);
                                            setAccountSecurity(false);
                                        }}>Profile</IonItem>
                                        <IonItem button onClick={() => {
                                            setProfile(false);
                                            setAccountSecurity(true);
                                        }}>Account security</IonItem>
                                        <IonItem>Others</IonItem>
                                    </IonCardContent>
                                </IonCard>
                            </div>
                        </IonCol>
                        <IonCol class="fullscreen" size="8.5">
                            <IonCard class="fullscreen">
                                <IonCardHeader>
                                    {profile && <IonCardTitle>Profile</IonCardTitle>}
                                    {accountSecurity && <IonCardTitle>Account Security</IonCardTitle>}
                                </IonCardHeader>
                                <IonCardContent>
                                    {profile &&
                                    <IonGrid>
                                        <IonRow class="fullscreen">
                                            <IonCol class="fullscreen" className="col">
                                                <IonLabel>Username</IonLabel>
                                                <IonItem>
                                                    <IonInput value={username}
                                                              onIonChange={e => setUsername(e.detail.value || '')}/>
                                                </IonItem>

                                                <div>Your username may appear around Noteit in the rooms or files
                                                    you
                                                    upload. You can modify any time
                                                </div>
                                                <IonButton color="secondary" onClick={() => {
                                                    //TODO: nu cea mai gandita validare (daca modific in ceva si apoi ma intorc la numele initial => nope)
                                                    if (username !== user.userName) {
                                                        usernameUpdate(username);
                                                    }
                                                }}>Update username</IonButton>
                                                <br/>
                                                <br/>

                                                <IonLabel>Full Name</IonLabel>
                                                <IonItem>
                                                    <IonInput value={fullname}
                                                              onIonChange={e => setFullname(e.detail.value || '')}/>
                                                </IonItem>

                                                <div>Your full name should remain the same but you can modify it if
                                                    needed.
                                                </div>
                                                <IonButton color="secondary" onClick={() => {
                                                    fullnameUpdate(fullname);
                                                }}>Update fullname</IonButton>
                                                <br/>
                                                <br/>

                                                <IonLabel>email</IonLabel>
                                                <IonItem>
                                                    <IonInput value={email}
                                                              onIonChange={e => setEmail(e.detail.value || '')}/>
                                                    {errorEmail &&
                                                    <IonNote className="note-error" slot="end">{errorEmail}</IonNote>}

                                                </IonItem>

                                                <div>You can have only one email to register in the Noteit
                                                    application.
                                                </div>
                                                {/*{errorEmail && <div className="div-error">{errorEmail}</div>}*/}
                                                <IonButton color="secondary" onClick={() => {
                                                    if (email.indexOf('@') > 0) {
                                                        emailUpdate(email);
                                                        setErrorEmail('');
                                                    } else {
                                                        setErrorEmail('Email must contain \'@\'');
                                                    }
                                                }}>Update email</IonButton>

                                            </IonCol>
                                        </IonRow>
                                    </IonGrid>
                                    }
                                    {accountSecurity &&
                                    <IonGrid>
                                        <IonList>
                                            <IonItem>
                                                <IonLabel>New password</IonLabel>
                                                <IonInput type="password"
                                                          onIonChange={e => {
                                                              setPassword(e.detail.value || '');
                                                              validatePassword(e.detail.value || '');
                                                          }}/>
                                                {errorPassword && <div className="note-error">{errorPassword}</div>}
                                            </IonItem>

                                            <IonItem>
                                                <IonLabel>Confirm new password</IonLabel>
                                                <IonInput type="password"
                                                          onIonChange={e => setConfirmedPassword(e.detail.value || '')}/>
                                                {errorPassword && errorPassword.indexOf('match') >= 0 &&
                                                <div className="note-error">{errorPassword}</div>}
                                            </IonItem>


                                        </IonList>
                                        <div>
                                            Make sure it's at least 16 characters OR 8 including a number, a lowercase
                                            letter and a special character.
                                        </div>
                                        <IonButton color="secondary" onClick={() => {
                                            validatePassword(password);
                                            if (errorPassword.length > 0) {
                                                setErrorPassword('Password is not strong enough');
                                            } else if (password === confirmedPassword) {
                                                passwordUpdate(password);
                                                setErrorPassword('');
                                                handleLogout();
                                            } else {
                                                setErrorPassword('Please make sure your passwords match!');
                                            }
                                        }}>Update password</IonButton>
                                    </IonGrid>
                                    }
                                </IonCardContent>
                            </IonCard>
                        </IonCol>
                    </IonRow>
                </IonGrid>
            </div>
        </IonContent>
    );
}
