import {getLogger} from "../shared";
import {useCallback, useContext, useEffect, useReducer} from "react";
import {AuthContext} from "../auth/AuthProvider";
import {updateEmail, updateFullName, updatePassword, updateUsername} from "./userApi";
import {getUser} from "../home/homeApi";
import {UserProps} from "../home/user";

const log = getLogger("useUserSettings");

const FETCH_USER_STARTED = 'FETCH_USER_STARTED';
const FETCH_USER_FAILED = 'FETCH_USER_FAILED';
const FETCH_USER_SUCCEEDED = 'FETCH_USER_SUCCEEDED';

const UPDATE_PASSWORD_STARTED = 'UPDATE_PASSWORD_STARTED';
const UPDATE_PASSWORD_SUCCEEDED = 'UPDATE_PASSWORD_SUCCEEDED';
const UPDATE_PASSWORD_FAILED = 'UPDATE_PASSWORD_FAILED';
const UPDATE_USERNAME_STARTED = 'UPDATE_USERNAME_STARTED';
const UPDATE_USERNAME_SUCCEEDED = 'UPDATE_USERNAME_SUCCEEDED';
const UPDATE_USERNAME_FAILED = 'UPDATE_USERNAME_FAILED';
const UPDATE_FULLNAME_STARTED = 'UPDATE_FULLNAME_STARTED';
const UPDATE_FULLNAME_SUCCEEDED = 'UPDATE_FULLNAME_SUCCEEDED';
const UPDATE_FULLNAME_FAILED = 'UPDATE_FULLNAME_FAILED';
const UPDATE_EMAIL_STARTED = 'UPDATE_EMAIL_STARTED';
const UPDATE_EMAIL_SUCCEEDED = 'UPDATE_EMAIL_SUCCEEDED';
const UPDATE_EMAIL_FAILED = 'UPDATE_EMAIL_FAILED';


type UpdatePasswordFn = (password: string) => void;
type UpdateUsernameFn = (password: string) => void;
type UpdateFullnameFn = (password: string) => void;
type UpdateEmailFn = (password: string) => void;

interface UserSettingsState {
    user: UserProps;
    updating: boolean,
    updatingPassword?: UpdatePasswordFn,
    updatingUsername?: UpdateUsernameFn,
    updatingFullname?: UpdateFullnameFn,
    updatingEmail?: UpdateEmailFn,
    updateError?: Error | null,
}

interface ActionProps {
    type: string,
    payload?: any,
}

const initialState: UserSettingsState = {
    user: {
        fullName: "",
        email: "",
        userName: ""
    },
    updating: false
}

const reducer: (state: UserSettingsState, action: ActionProps) => UserSettingsState =
    (state, {type, payload}) => {
        switch (type) {
            case UPDATE_PASSWORD_STARTED:
                return {...state, updating: true, updateError: null};
            case UPDATE_PASSWORD_SUCCEEDED:
                return {...state, updating: false, updateError: null};
            case UPDATE_PASSWORD_FAILED:
                return {...state, updating: false, updateError: payload.error};
            case UPDATE_USERNAME_STARTED:
                return {...state, updating: true, updateError: null};
            case UPDATE_USERNAME_SUCCEEDED:
                return {...state, updating: false, updateError: null};
            case UPDATE_USERNAME_FAILED:
                return {...state, updating: false, updateError: payload.error};
            case UPDATE_FULLNAME_STARTED:
                return {...state, updating: true, updateError: null};
            case UPDATE_FULLNAME_SUCCEEDED:
                return {...state, updating: false, updateError: null};
            case UPDATE_FULLNAME_FAILED:
                return {...state, updating: false, updateError: payload.error};
            case UPDATE_EMAIL_STARTED:
                return {...state, updating: true, updateError: null};
            case UPDATE_EMAIL_SUCCEEDED:
                return {...state, updating: false, updateError: null};
            case UPDATE_EMAIL_FAILED:
                return {...state, updating: false, updateError: payload.error};
            case FETCH_USER_STARTED:
                return {...state, fetchingUser: true, fetchingUserError: null}
            case FETCH_USER_FAILED:
                return {...state, fetchingUsers: false, fetchingUserError: payload.error}
            case FETCH_USER_SUCCEEDED:
                return {...state, fetchingUser: false, user: payload.user}
            default:
                return state;
        }
    }

export const useUserSettings = () => {
    const {token, logout} = useContext(AuthContext);
    const [state, dispatch] = useReducer(reducer, initialState);

    const passwordUpdate = useCallback<UpdatePasswordFn>(updatePasswordCallback, [token]);
    const usernameUpdate = useCallback<UpdateUsernameFn>(updateUsernameCallback, [token]);
    const fullnameUpdate = useCallback<UpdateFullnameFn>(updateFullnameCallback, [token]);
    const emailUpdate = useCallback<UpdateEmailFn>(updateEmailCallback, [token]);

    useEffect(fetchUserEffect, [token]);

    return { state, passwordUpdate, usernameUpdate, fullnameUpdate, emailUpdate };


    function fetchUserEffect() {
        let canceled = false;
        fetchUser();
        return () => {
            canceled = true;
        }

        async function fetchUser() {
            if (!token?.trim()) {
                return;
            }
            try {
                log(`fetchUser started`);
                dispatch({type: FETCH_USER_STARTED});

                let result = await getUser(token);
                let result2 = {userName: result.username, email: result.email, fullName: result.full_name}

                log('fetchUser succeeded');
                if (!canceled) {
                    dispatch({type: FETCH_USER_SUCCEEDED, payload: {user: result2}});
                }
            } catch (error) {
                log('fetchUser failed');
                dispatch({type: FETCH_USER_FAILED, payload: {error}});
            }
        }
    }

    async function updatePasswordCallback(password: string) {
        try {
            log("updating password started");
            dispatch({type: UPDATE_PASSWORD_STARTED});
            if (password === "") {
                log("updating password failed");
                dispatch({type: UPDATE_PASSWORD_FAILED, payload: {error: {message: "Invalid password!"}}});
                return;
            }
            const response = await updatePassword(token, password);
            log("updating password succeeded");
            dispatch({type: UPDATE_PASSWORD_SUCCEEDED, payload: {}});
        } catch (error) {
            log("updating password failed");
            dispatch({type: UPDATE_PASSWORD_FAILED, payload: {error}});
        }
    }

    async function updateUsernameCallback(username: string) {
        try {
            log("updating username started");
            dispatch({type: UPDATE_USERNAME_STARTED});
            //TODO: validare user exista deja
            if (username === "") {
                log("updating username failed");
                dispatch({type: UPDATE_USERNAME_FAILED, payload: {error: {message: "Username cannot be empty!"}}});
                return;
            }
            const response = await updateUsername(token, username);
            log("updating username succeeded");
            dispatch({type: UPDATE_USERNAME_SUCCEEDED, payload: {}});
        } catch (error) {
            log("updating username failed");
            dispatch({type: UPDATE_USERNAME_FAILED, payload: {error}});
        }
    }

    async function updateFullnameCallback(fullname: string) {
        try {
            log("updating fullname started");
            dispatch({type: UPDATE_FULLNAME_STARTED});
            //TODO: validare fullname
            if (fullname === "") {
                log("updating fullname failed");
                dispatch({type: UPDATE_FULLNAME_FAILED, payload: {error: {message: "Fullname cannot be empty!"}}});
                return;
            }
            const response = await updateFullName(token, fullname);
            log("updating fullname succeeded");
            dispatch({type: UPDATE_FULLNAME_SUCCEEDED, payload: {}});
        } catch (error) {
            log("updating fullname failed");
            dispatch({type: UPDATE_FULLNAME_FAILED, payload: {error}});
        }
    }

    async function updateEmailCallback(email: string) {
        try {
            log("updating email started");
            dispatch({type: UPDATE_EMAIL_STARTED});
            //TODO: validare email
            if (email === "" || email.indexOf('@') < 0) {
                log("updating email failed");
                dispatch({type: UPDATE_EMAIL_FAILED, payload: {error: {message: "Email cannot be empty and must contains @"}}});
                return;
            }
            const response = await updateEmail(token, email);
            log("updating email succeeded");
            dispatch({type: UPDATE_EMAIL_SUCCEEDED, payload: {}});
        } catch (error) {
            log("updating email failed");
            dispatch({type: UPDATE_EMAIL_FAILED, payload: {error}});
        }
    }


}