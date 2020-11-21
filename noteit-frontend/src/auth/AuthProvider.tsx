import React, { useCallback, useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import { getLogger } from '../shared';
import { login as loginApi, signup as signupApi } from './authApi';

const log = getLogger('AuthProvider');

type LoginFn = (username?: string, password?: string) => void;
type SignUpFn = (username?: string, password?: string, full_name?: string, email?: string) => void;

export interface AuthState {
  authenticationError: Error | null;
  isAuthenticated: boolean;
  isAuthenticating: boolean;
  login?: LoginFn;
  pendingAuthentication?: boolean;
  username?: string;
  password?: string;
  token: string;
}

export interface SignUpState {
  signupError: Error | null;
  isSigned: boolean;
  isSigning: boolean;
  signup?: SignUpFn;
  pendingSigning: boolean;
  username?: string;
  password?: string;
  full_name?: string;
  email?: string;
}

const initialState: AuthState = {
  isAuthenticated: false,
  isAuthenticating: false,
  authenticationError: null,
  pendingAuthentication: false,
  token: '',
};

const signUpInitialState: SignUpState = {
  isSigned: false,
  isSigning: false,
  signupError: null,
  pendingSigning: false,
}

export const AuthContext = React.createContext<AuthState>(initialState);
export const SignUpContext = React.createContext<SignUpState>(signUpInitialState);


interface AuthProviderProps {
  children: PropTypes.ReactNodeLike,
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {

  const [signUpState, setStateSignUp] = useState<SignUpState>(signUpInitialState);
  const { isSigned, isSigning, signupError, pendingSigning } = signUpState;
  const signup = useCallback<SignUpFn>(signupCallback, [])
  useEffect(signupEffect, [pendingSigning]);
  const valueS = {isSigned, signup, isSigning, signupError, pendingSigning}

  const [state, setState] = useState<AuthState>(initialState);
  const { isAuthenticated, isAuthenticating, authenticationError, pendingAuthentication, token } = state;
  const login = useCallback<LoginFn>(loginCallback, []);
  useEffect(authenticationEffect, [pendingAuthentication]);
  
  const value = { isAuthenticated, login, isAuthenticating, authenticationError, token};
  log('render');
  return (
    <SignUpContext.Provider value = {valueS}>
      <AuthContext.Provider value={value}>
        {children}
      </AuthContext.Provider>
    </SignUpContext.Provider>
  );

  function signupCallback(username?: string, password?: string, full_name?: string, email?: string): void {
    log('sign up');
    setStateSignUp({
      ...signUpState,
      pendingSigning: true,
      username,
      password,
      full_name,
      email
    });
  }

  function loginCallback(username?: string, password?: string): void {
    log('login');
    setState({
      ...state,
      pendingAuthentication: true,
      username,
      password
    });
  }

  function signupEffect() {
    let canceled = false;
    sign();
    return () => {
      canceled = true;
    }

    async function sign() {
      if (!pendingSigning) {
        log('sign, !pendingSigning, return');
        return;
      }
      try {
        log('Signing...');
        setStateSignUp({
          ...signUpState,
          isSigning: true,
        });
        const { username, password, full_name, email } = signUpState;
        const {message, success} = await signupApi(username, password, full_name, email);
        if (canceled) {
          return;
        }
        log('Sign Up succeeded');
        if(success === true) {
          setStateSignUp({
            ...signUpState,
            pendingSigning: false,
            isSigned: true,
            isSigning: false,
          });
        }
      } catch (error) {
        if (canceled) {
          return;
        }
        log('Sign Up failed');
        setStateSignUp({
          ...signUpState,
          signupError: error,
          pendingSigning: false,
          isSigning: false,
        });
      }
    }
  }

  function authenticationEffect() {
    let canceled = false;
    authenticate();
    return () => {
      canceled = true;
    }

    async function authenticate() {
      if (!pendingAuthentication) {
        log('authenticate, !pendingAuthentication, return');
        return;
      }
      try {
        log('authenticate...');
        setState({
          ...state,
          isAuthenticating: true,
        });
        const { username, password } = state;
        const { accessToken, tokenType } = await loginApi(username, password);
        const token = accessToken;
        console.log("Token= ",token)
        if (canceled) {
          return;
        }
        log('authenticate succeeded');
        setState({
          ...state,
          token,
          pendingAuthentication: false,
          isAuthenticated: true,
          isAuthenticating: false,
        });
      } catch (error) {
        if (canceled) {
          return;
        }
        log('authenticate failed');
        setState({
          ...state,
          authenticationError: error,
          pendingAuthentication: false,
          isAuthenticating: false,
        });
      }
    }
  }
};
