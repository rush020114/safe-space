import { axiosInstance } from "./axiosInstance"

/**
 * 게시글 조회
 * @returns 게시글 목록
 */
export const getPostListApi = async () => {
  const res = await axiosInstance.get('/posts');

  return res;
}

/**
 * 게시글 상세 조회
 * @param {*} id 게시글 아이디
 * @returns 게시글 상세
 */
export const getPostDetailApi = async id => {
  const res = await axiosInstance.get(`/posts/${id}`);

  return res;
}