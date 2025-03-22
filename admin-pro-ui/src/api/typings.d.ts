// @ts-ignore
/* eslint-disable */

declare namespace API {
  type CurrentUser = {
    username?: string;
    authorities?: string[];
    avatar?: string;
  };

  type Response<T> = {
    status: string;
    success: boolean;
    errCode: string;
    errMessage: string;
    data: T;
  }

}


declare namespace Account {

  type LoginResponse = {
    token: string;
    username: string;
    authorities: string[];
  };


  type LoginRequest = {
    username?: string;
    password?: string;
    type?: string;
  };
}

