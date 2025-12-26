import axios from "axios";

const instance = axios.create({
  baseURL: "/api",
  timeout: 10000,
});

// ✅ 统一拍扁：如果返回是 [ {code, data...} ] 就取第 0 个
instance.interceptors.response.use(
  (resp) => {
    const body = resp.data;

    if (
      Array.isArray(body) &&
      body.length === 1 &&
      body[0] &&
      typeof body[0] === "object" &&
      Object.prototype.hasOwnProperty.call(body[0], "code")
    ) {
      return body[0];
    }

    return body;
  },
  (err) => Promise.reject(err)
);

export default instance;
