import {useCallback, useContext, useEffect, useReducer} from "react";
import {getLogger} from "../shared";
import {AuthContext} from "../auth/AuthProvider";
import {RoomProps} from "../home/room";
import {getRooms, getRoomsByTags, getTags} from "./discoverApi";

const log = getLogger("useHome");

export type HideCreateRoomFn = () => void;
export type ChangeSearchFn = (value: string) => void;
export type FilterFn = (value: string[]) => void;

export interface TagProps {
    id: string;
    name: string;
    onCheck?: HideCreateRoomFn;
}

interface DiscoverState {
    rooms: RoomProps[];
    tags: TagProps[];
    fetchingRooms: boolean;
    fetchingRoomsError?: Error | null;
    fetchingTags: boolean;
    fetchingTagsError?: Error | null;
}

const initialState: DiscoverState = {
    rooms: [],
    tags: [],
    fetchingRooms: false,
    fetchingTags: false
};

interface ActionProps {
    type: string,
    payload?: any,
}

const FETCH_ROOMS_STARTED = 'FETCH_ROOMS_STARTED';
const FETCH_ROOMS_FAILED = 'FETCH_ROOMS_FAILED';
const FETCH_ROOMS_SUCCEEDED = 'FETCH_ROOMS_SUCCEEDED';
const FETCH_TAGS_STARTED = 'FETCH_TAGS_STARTED';
const FETCH_TAGS_FAILED = 'FETCH_TAGS_FAILED';
const FETCH_TAGS_SUCCEEDED = 'FETCH_TAGS_SUCCEEDED';

const reducer: (state: DiscoverState, action: ActionProps) => DiscoverState =
    (state, {type, payload}) => {
        switch (type) {
            case FETCH_ROOMS_STARTED:
                return {...state, fetchingRooms: true, fetchingRoomsError: null}
            case FETCH_ROOMS_FAILED:
                return {...state, fetchingRooms: false, fetchingRoomsError: payload.error}
            case FETCH_ROOMS_SUCCEEDED:
                let result = payload.rooms;
                return {...state, fetchingRooms: false, rooms: result}
            case FETCH_TAGS_STARTED:
                return {...state, fetchingTags: true, fetchingTagsError: null}
            case FETCH_TAGS_FAILED:
                return {...state, fetchingTags: false, fetchingTagsError: payload.error}
            case FETCH_TAGS_SUCCEEDED:
                return {...state, fetchingRooms: false, tags: payload.tags}
            default:
                return state;
        }
    };

export const useDiscover = () => {
    const {token} = useContext(AuthContext);
    const [state, dispatch] = useReducer(reducer, initialState);
    const filterRooms = useCallback<FilterFn>(filterCallback, [token]);
    const searchRooms = useCallback<ChangeSearchFn>(searchRoomsCallback, [token]);
    useEffect(fetchRoomsEffect, [token]);
    useEffect(fetchTagsEffect, [token]);
    return {state, filterRooms, searchRooms};

    function filterCallback(filterTags: string[]) {
        log('filterByTags');
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
                log(`fetchRoomsByTags started`);
                dispatch({type: FETCH_ROOMS_STARTED});
                // server get rooms
                let result;
                if (filterTags.length > 0) {
                    result = await getRoomsByTags(token, filterTags);
                } else {
                    result = await getRooms(token);
                }
                log('fetchRoomsByTags succeeded');
                if (!canceled) {
                    dispatch({type: FETCH_ROOMS_SUCCEEDED, payload: {rooms: result}});
                }
            } catch (error) {
                log('fetchRoomsByTags failed');
                dispatch({type: FETCH_ROOMS_FAILED, payload: {error}});
            }
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

    function searchRoomsCallback(text: string) {
        let canceled = false;
        searchRooms(text);
        return () => {
            canceled = true;
        }

        async function searchRooms(text: string) {
            if (!token?.trim()) {
                return;
            }
            try {
                log(`searchRooms started`);
                dispatch({type: FETCH_ROOMS_STARTED});
                // server get rooms
                console.log(text);
                let result = await getRooms(token, text);
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

    function fetchTagsEffect() {
        let canceled = false;
        fetchTags();
        return () => {
            canceled = true;
        }

        async function fetchTags() {
            if (!token?.trim()) {
                return;
            }
            try {
                log(`fetchTags started`);
                dispatch({type: FETCH_TAGS_STARTED});
                // server get rooms
                let result = await getTags(token);
                log('fetchTags succeeded');
                if (!canceled) {
                    dispatch({type: FETCH_TAGS_SUCCEEDED, payload: {tags: result}});
                }
            } catch (error) {
                log('fetchTags failed');
                dispatch({type: FETCH_ROOMS_FAILED, payload: {error}});
            }
        }
    }
}

