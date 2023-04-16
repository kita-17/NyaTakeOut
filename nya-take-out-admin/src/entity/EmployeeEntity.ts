export interface EmployeeEntity {
    //员工id
    id: string;
    key: string;
    storeId: string | number,
    //员工名字
    name: string;
    //密码
    password: string;
    //员工性别
    sex: Number;
    //员工账号名
    username: string;
    //员工手机号
    phone: string;
    //账号状态
    status: boolean;
    //员工权限
    permissions: Number;
    //员工身份证号码
    idNumber: Number;
}