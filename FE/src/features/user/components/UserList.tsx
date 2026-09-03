import type { UserResponse } from '../types/user.types'

interface UserListProps {
  users: UserResponse[]
  isLoading: boolean
  onEdit: (user: UserResponse) => void
  onDisable: (user: UserResponse) => void
  onEnable: (user: UserResponse) => void
}

export function UserList({ users, isLoading, onEdit, onDisable, onEnable }: UserListProps) {
  if (isLoading) {
    return <p className="user-muted">Đang tải...</p>
  }

  if (users.length === 0) {
    return <p className="user-muted">Chưa có user nào.</p>
  }

  return (
    <div className="user-table-wrap">
      <table className="user-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Tên</th>
            <th>Email</th>
            <th>Công ty</th>
            <th>Vai trò</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.id}>
              <td>{user.id}</td>
              <td>{user.name}</td>
              <td>{user.email}</td>
              <td>{user.company?.name ?? '—'}</td>
              <td>{user.roles.map((role) => role.name).join(', ') || '—'}</td>
              <td>{user.active ? 'Hoạt động' : 'Đã tắt'}</td>
              <td className="user-actions">
                <button type="button" onClick={() => onEdit(user)}>
                  Sửa
                </button>
                {user.active ? (
                  <button type="button" className="user-btn-danger" onClick={() => onDisable(user)}>
                    Vô hiệu hóa
                  </button>
                ) : (
                  <button type="button" onClick={() => onEnable(user)}>
                    Kích hoạt
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
