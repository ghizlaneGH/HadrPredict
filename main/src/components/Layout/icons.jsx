import { Path } from "leaflet";
import {
  FaHome,
  FaUpload ,
  FaThLarge,
  FaMap,
  FaSignOutAlt,
} from "react-icons/fa";

export const menuItems = [
  { icon: <FaHome />, text: "Home", path: "/LoginPage" },
  { icon: <FaThLarge />, text: "Dashboard", path: "/DashGlobal" },
  { icon: <FaMap />, text: "Cartegraphie", path: "/GeoCarte" },
  { icon: <FaUpload  />, text: "Importer", path: "/FormPage" },
  { icon: <FaSignOutAlt />, text: "Logout", path: "/LoginPage" },
];
