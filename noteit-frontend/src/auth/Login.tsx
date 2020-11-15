import React, {useEffect, useState} from "react";
import {getMessage} from "./authApi";
import {
    IonLabel
} from '@ionic/react';

export const Login: React.FC = () => {
    let [text, setText] = useState('');
    useEffect(() => {
        const fetchMessage = async () => {
            let s = await getMessage();
            setText(s);
        };
        fetchMessage();
    }, []);
    return (
        <IonLabel>Message: {text}</IonLabel>
    );
}

