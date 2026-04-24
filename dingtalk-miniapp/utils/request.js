const { apiBase } = require('../config')

function normalize(body) {
  if (typeof body === 'string') {
    try {
      body = JSON.parse(body)
    } catch (err) {
      return body
    }
  }

  if (Array.isArray(body) && body.length === 1 && body[0] && body[0].code !== undefined) {
    return body[0]
  }
  return body
}

function request(path, method = 'GET', data = {}) {
  const api = typeof dd !== 'undefined' ? dd : my
  const requestMethod = api.httpRequest || api.request
  const upperMethod = method.toUpperCase()

  return new Promise((resolve, reject) => {
    requestMethod({
      url: `${apiBase}${path}`,
      method: upperMethod,
      data: upperMethod === 'GET' ? data : JSON.stringify(data),
      dataType: 'json',
      headers: {
        'content-type': 'application/json'
      },
      header: {
        'content-type': 'application/json'
      },
      success(res) {
        resolve(normalize(res.data))
      },
      fail(err) {
        reject(err)
      }
    })
  })
}

module.exports = {
  get(path, data) {
    return request(path, 'GET', data)
  },
  post(path, data) {
    return request(path, 'POST', data)
  }
}
