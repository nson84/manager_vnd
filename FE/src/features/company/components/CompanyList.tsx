import type { CompanyResponse } from '../types/company.types'

interface CompanyListProps {
  companies: CompanyResponse[]
  isLoading: boolean
  onEdit: (company: CompanyResponse) => void
  onDisable: (company: CompanyResponse) => void
  onEnable: (company: CompanyResponse) => void
}

export function CompanyList({
  companies,
  isLoading,
  onEdit,
  onDisable,
  onEnable,
}: CompanyListProps) {
  if (isLoading) {
    return <p className="company-muted">Đang tải...</p>
  }

  if (companies.length === 0) {
    return <p className="company-muted">Chưa có công ty nào.</p>
  }

  return (
    <div className="company-table-wrap">
      <table className="company-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Tên</th>
            <th>Địa chỉ</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          {companies.map((company) => (
            <tr key={company.id}>
              <td>{company.id}</td>
              <td>
                <div>{company.name}</div>
                {company.description && (
                  <div className="company-muted">{company.description}</div>
                )}
              </td>
              <td>{company.address || '—'}</td>
              <td>
                <span className={`company-badge ${company.active ? 'on' : 'off'}`}>
                  {company.active ? 'Hoạt động' : 'Đã tắt'}
                </span>
              </td>
              <td className="company-actions">
                <button type="button" onClick={() => onEdit(company)}>
                  Sửa
                </button>
                {company.active ? (
                  <button type="button" className="danger" onClick={() => onDisable(company)}>
                    Vô hiệu hóa
                  </button>
                ) : (
                  <button type="button" className="success" onClick={() => onEnable(company)}>
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
