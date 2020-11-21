import {useCallback, useContext, useEffect, useReducer} from "react";
import {getLogger} from "../shared";
import {RoomProps} from "./room";
import {UserProps} from "./user";
import {NotificationProps} from "./recentfiles";
import {AuthContext} from "../auth/AuthProvider";
import {addRoom, getRooms, getUser} from "./homeApi";

const log = getLogger("useHome");

export type CreateRoomFn = (name: string) => void;
export type HideCreateRoomFn = () => void;

interface HomeState {
    rooms: RoomProps[];
    user: UserProps;
    recentFiles: NotificationProps[];
    showAddRoom: boolean;
    creating: boolean;
    creatingError?: Error | null;
    fetchingRooms: boolean;
    fetchingRoomsError?: Error | null;
    fetchingRecentFiles: boolean;
    fetchingRecentFilesError?: Error | null;
    fetchingUser: boolean;
    fetchingUserError?: Error | null;
}

const initialState: HomeState = {
    rooms: [],
    user: {
        fullName: "",
        email: "",
        userName: ""
    },
    recentFiles: [],
    showAddRoom: false,
    creating: false,
    fetchingRooms: false,
    fetchingRecentFiles: false,
    fetchingUser: false
};

interface ActionProps {
    type: string,
    payload?: any,
}

const SHOW_ADD_ROOM = 'SHOW_ADD_ROOM';
const CREATE_ROOM_STARTED = 'CREATE_ROOM_STARTED';
const CREATE_ROOM_FAILED = 'CREATE_ROOM_FAILED';
const CREATE_ROOM_SUCCEEDED = 'CREATE_ROOM_SUCCEEDED';
const HIDE_ADD_ROOM = 'HIDE_ADD_ROOM';
const FETCH_ROOMS_STARTED = 'FETCH_ROOMS_STARTED';
const FETCH_ROOMS_FAILED = 'FETCH_ROOMS_FAILED';
const FETCH_ROOMS_SUCCEEDED = 'FETCH_ROOMS_SUCCEEDED';
const FETCH_RECENT_FILES_STARTED = 'FETCH_RECENT_FILES_STARTED';
const FETCH_RECENT_FILES_FAILED = 'FETCH_RECENT_FILES_FAILED';
const FETCH_RECENT_FILES_SUCCEEDED = 'FETCH_RECENT_FILES_SUCCEEDED';
const FETCH_USER_STARTED = 'FETCH_USER_STARTED';
const FETCH_USER_FAILED = 'FETCH_USER_FAILED';
const FETCH_USER_SUCCEEDED = 'FETCH_USER_SUCCEEDED';

const reducer: (state: HomeState, action: ActionProps) => HomeState =
    (state, {type, payload}) => {
        switch (type) {
            case SHOW_ADD_ROOM:
                return {...state, showAddRoom: true}
            case HIDE_ADD_ROOM:
                return {...state, showAddRoom: false, creatingError: null}
            case CREATE_ROOM_STARTED:
                return {...state, creating: true, creatingError: null}
            case CREATE_ROOM_FAILED:
                return {...state, creating: false, creatingError: payload.error}
            case CREATE_ROOM_SUCCEEDED:
                let rooms = state.rooms;
                const room = payload.room;
                const index = rooms.findIndex(r => r.id === room.id);
                if (index === -1) {
                    rooms.push(room);
                } else {
                    rooms[index] = room;
                }
                return {...state, creating: false, rooms: rooms}
            case FETCH_ROOMS_STARTED:
                return {...state, fetchingRooms: true, fetchingRoomsError: null}
            case FETCH_ROOMS_FAILED:
                return {...state, fetchingRooms: false, fetchingRoomsError: payload.error}
            case FETCH_ROOMS_SUCCEEDED:
                let result = state.rooms;
                payload.rooms.forEach((room: RoomProps) => {
                    const index = state.rooms.findIndex(r => r.id === room.id);
                    if (index === -1) {
                        result.push(room);
                    } else {
                        result[index] = room;
                    }
                })
                return {...state, fetchingRooms: false, rooms: result}
            case FETCH_RECENT_FILES_STARTED:
                return {...state, fetchingRecentFiles: true, fetchingRecentFilesError: null}
            case FETCH_RECENT_FILES_FAILED:
                return {...state, fetchingRecentFiles: false, fetchingRecentFilesError: payload.error}
            case FETCH_RECENT_FILES_SUCCEEDED:
                let recentFiles = state.recentFiles;
                payload.recentFiles.forEach((file: NotificationProps) => {
                    const index = state.recentFiles.findIndex(f => f.id === file.id);
                    if (index === -1) {
                        recentFiles.push(file);
                    } else {
                        recentFiles[index] = file;
                    }
                })
                return {...state, fetchingRecentFiles: false, recentFiles: recentFiles}
            case FETCH_USER_STARTED:
                return {...state, fetchingUser: true, fetchingUserError: null}
            case FETCH_USER_FAILED:
                return {...state, fetchingUsers: false, fetchingUserError: payload.error}
            case FETCH_USER_SUCCEEDED:
                return {...state, fetchingUser: false, user: payload.user}
            default:
                return state;
        }
    };

export const useHome = () => {
    const {token} = useContext(AuthContext);
    const [state, dispatch] = useReducer(reducer, initialState);
    const createRoom = useCallback<CreateRoomFn>(createRoomCallback, [token]);
    const hideCreateRoom = useCallback<HideCreateRoomFn>(hideCreateRoomCallback, []);
    const showCreateRoom = useCallback<HideCreateRoomFn>(showCreateRoomCallback, []);
    useEffect(fetchRoomsEffect, [token]);
    useEffect(fetchRecentFilesEffect, [token]);
    useEffect(fetchUserEffect, [token]);
    return {state, createRoom, hideCreateRoom, showCreateRoom};

    async function hideCreateRoomCallback() {
        log('hideRoomPopover');
        dispatch({type: HIDE_ADD_ROOM});
    }

    async function showCreateRoomCallback() {
        log('showRoomPopover');
        dispatch({type: SHOW_ADD_ROOM});
    }

    async function createRoomCallback(name: string) {
        try {
            log('createRoom started');
            dispatch({type: CREATE_ROOM_STARTED});
            if (name === "") {
                log('createRoom failed');
                dispatch({type: CREATE_ROOM_FAILED, payload: {error: {message: "Room name must be entered"}}});
                return;
            }
            const createdRoom = await addRoom(token, {id:"", name});
            // const createdRoom = {id: Date.now(), name: name};
            log('createRoom succeeded');

            dispatch({type: CREATE_ROOM_SUCCEEDED, payload: {room: createdRoom}});
        } catch (error) {
            log('createRoom failed');
            dispatch({type: CREATE_ROOM_FAILED, payload: {error}});
        }
    }

    function fetchRoomsEffect() {
        let canceled = false;
        fetchRooms();
        return () => {
            canceled = true;
        }

        async function fetchRooms() {
            if (!token?.trim()) {
                return;
            }
            try {
                log(`fetchRooms started`);
                dispatch({type: FETCH_ROOMS_STARTED});
                // server get rooms
                let result = await getRooms(token);
                // let result: RoomProps[] = [];
                log('fetchRooms succeeded');
                if (!canceled) {
                    dispatch({type: FETCH_ROOMS_SUCCEEDED, payload: {rooms: result}});
                }
            } catch (error) {
                log('fetchRooms failed');
                dispatch({type: FETCH_ROOMS_FAILED, payload: {error}});
            }
        }
    }

    function fetchRecentFilesEffect() {
        let canceled = false;
        fetchRecentFiles();
        return () => {
            canceled = true;
        }

        async function fetchRecentFiles() {
            if (!token?.trim()) {
                return;
            }
            try {
                log(`fetchRecentFiles started`);
                dispatch({type: FETCH_RECENT_FILES_STARTED});
                // server get recent files
                // let result = await getRecentFiles();
                let result: NotificationProps[] = [];
                log('fetchRecentFiles succeeded');
                if (!canceled) {
                    dispatch({type: FETCH_RECENT_FILES_SUCCEEDED, payload: {recentFiles: result}});
                }
            } catch (error) {
                log('fetchRecentFiles failed');
                dispatch({type: FETCH_RECENT_FILES_FAILED, payload: {error}});
            }
        }
    }

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
                // server get user data
                let result = await getUser(token);
                let result2 = {userName: result.username, email: result.email, fullName: result.full_name}
                // let result: UserProps = {fullName: "Full Name", email: "email", userName: "Username"};
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
}

