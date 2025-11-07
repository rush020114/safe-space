import { configureStore } from "@reduxjs/toolkit"
import authSlice from "./authSlice"

// slice들을 저장할 저장소이다.(등록을 마치면 필요한 컴포넌트에서 손쉽게 사용 가능)
// configureStore함수는 중앙 저장소를 설정할 수 있다.
export const store = configureStore({
  // reducer에는 중앙 저장소에서 사용할 state값들을 저장한다.
  reducer: {
    auth: authSlice.reducer
  }
});
