import {useCallback, useContext, useEffect, useReducer} from "react";
import {getLogger} from "../shared";
import {AuthContext} from "../auth/AuthProvider";
import {RoomProps} from "../home/room";
import {getRooms, getRoomsByTags} from "./discoverApi";

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
    searchText: string;
    // notificationsPage: number;
    // nextPage?: NextPageFn;
    // previousPage?: NextPageFn;
    // hasMoreNotifications: boolean;
    // previousNotifications: boolean;
}

const initialState: DiscoverState = {
    rooms: [],
    tags: [],
    fetchingRooms: false,
    fetchingTags: false,
    searchText: ""
    // notificationsPage: 0,
    // hasMoreNotifications: false,
    // previousNotifications: false
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
const CHANGE_SEARCH = 'CHANGE_SEARCH';

const reducer: (state: DiscoverState, action: ActionProps) => DiscoverState =
    (state, {type, payload}) => {
        switch (type) {
            case FETCH_ROOMS_STARTED:
                return {...state, fetchingRooms: true, fetchingRoomsError: null}
            case FETCH_ROOMS_FAILED:
                return {...state, fetchingRooms: false, fetchingRoomsError: payload.error}
            case FETCH_ROOMS_SUCCEEDED:
                // let result = state.rooms;
                let result = payload.rooms;
                // payload.rooms.forEach((room: RoomProps) => {
                //     const index = state.rooms.findIndex(r => r.id === room.id);
                //     if (index === -1) {
                //         result.push(room);
                //     } else {
                //         result[index] = room;
                //     }
                // })
                return {...state, fetchingRooms: false, rooms: result}
            case FETCH_TAGS_STARTED:
                return {...state, fetchingTags: true, fetchingTagsError: null}
            case FETCH_TAGS_FAILED:
                return {...state, fetchingTags: false, fetchingTagsError: payload.error}
            case FETCH_TAGS_SUCCEEDED:
                return {...state, fetchingRooms: false, tags: payload.tags}
            // case FETCH_NEXT_PAGE:
            //     const nextPage = state.notificationsPage + 1;
            //     return {
            //         ...state,
            //         notificationsPage: nextPage,
            //         previousNotifications: true
            //     };
            // case FETCH_PREVIOUS_PAGE:
            //     const currentPage = state.notificationsPage - 1;
            //     let previous = true;
            //     if (currentPage === 0) {
            //         previous = false;
            //     }
            //     return {
            //         ...state,
            //         notificationsPage: currentPage,
            //         previousNotifications: previous,
            //         hasMoreNotifications: true
            //     };
            case CHANGE_SEARCH:
                return {...state, searchText: payload.search}
            default:
                return state;
        }
    };

export const useDiscover = () => {
    const {token} = useContext(AuthContext);
    const [state, dispatch] = useReducer(reducer, initialState);
    const {searchText} = state;
    // const nextPage = useCallback<NextPageFn>(fetchNextPage, [token]);
    // const previousPage = useCallback<NextPageFn>(fetchPreviousPage, [token]);
    const changeSearch = useCallback<ChangeSearchFn>(changeSearchCallback, [token]);
    const filterRooms = useCallback<FilterFn>(filterCallback, [token]);
    useEffect(fetchRoomsEffect, [token, searchText]);
    useEffect(fetchTagsEffect, [token]);
    return {state, changeSearch, filterRooms};

    async function changeSearchCallback(value: string) {
        log('changeSearch');
        dispatch({type: CHANGE_SEARCH, payload: {search: value}});
    }

    function filterCallback(filterTags: string[]) {
        log('filterByTags');
        let canceled = false;
        // if (filterTags.length > 0) {
        fetchRooms();
        // }
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
                    result = await getRooms(token, searchText);
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

    // async function fetchNextPage() {
    //     log('fetchNextPage');
    //     dispatch({type: FETCH_NEXT_PAGE});
    // }

    // async function fetchPreviousPage() {
    //     log('fetchPreviousPage');
    //     dispatch({type: FETCH_PREVIOUS_PAGE});
    // }

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
                let result = await getRooms(token, searchText);
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
                // let result = await getTags(token);
                let result: TagProps[] = [{id: "1", name: "tag1"}, {
                    id: "2",
                    name: "tag2",
                }, {id: "3", name: "cs"}];
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

