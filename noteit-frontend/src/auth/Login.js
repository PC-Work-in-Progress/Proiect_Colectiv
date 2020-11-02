import React, {useEffect, useState} from "react";
import {getMessage} from "./authApi";
import InputLabel from "@material-ui/core/InputLabel";

function Login() {
    let [text, setText] = useState('');
    useEffect(() => {
        const fetchMessage = async () => {
            let s = await getMessage();
            setText(s);
        };
        fetchMessage();
    }, []);
    return (
        <div>
            <header>
                <InputLabel>Message: {text}</InputLabel>
            </header>
        </div>
    );
}

export default Login;
